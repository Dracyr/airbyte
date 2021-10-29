/*
 * Copyright (c) 2021 Airbyte, Inc., all rights reserved.
 */

package io.airbyte.config.specs;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.JsonNode;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;
import io.airbyte.commons.io.IOs;
import io.airbyte.commons.json.Jsons;
import io.airbyte.commons.util.MoreIterators;
import io.airbyte.commons.yaml.Yamls;
import io.airbyte.config.EnvConfigs;
import io.airbyte.protocol.models.ConnectorSpecification;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FetchSeedConnectorSpecs {

  private static final String DOCKER_REPOSITORY_FIELD = "dockerRepository";
  private static final String DOCKER_IMAGE_TAG_FIELD = "dockerImageTag";
  private static final String SPEC_BUCKET_NAME = new EnvConfigs().getSpecCacheBucket();

  private static final Logger LOGGER = LoggerFactory.getLogger(FetchSeedConnectorSpecs.class);

  private static final Options OPTIONS = new Options();
  private static final Option SEED_ROOT_OPTION = new Option("o", "seed-root", true, "path to where seed resource files are stored");

  private static Storage storage;

  private Storage getStorage() {
    if (storage == null) {
      storage = StorageOptions.getDefaultInstance().getService();
    }
    return storage;
  }

  static {
    SEED_ROOT_OPTION.setRequired(true);
    OPTIONS.addOption(SEED_ROOT_OPTION);
  }

  public static void main(final String[] args) throws Exception {
    final CommandLine parsed = parse(args);
    final Path outputRoot = Path.of(parsed.getOptionValue(SEED_ROOT_OPTION.getOpt()));

    final FetchSeedConnectorSpecs fetchSeedConnectorSpecs = new FetchSeedConnectorSpecs();
    fetchSeedConnectorSpecs.run(outputRoot, ConnectorType.SOURCE);
    fetchSeedConnectorSpecs.run(outputRoot, ConnectorType.DESTINATION);
  }

  public void run(final Path seedRoot, final ConnectorType connectorType) throws IOException {
    LOGGER.info("Updating seeded {} definition specs if necessary...", connectorType.name());

    final String seedDefinitionsYaml = IOs.readFile(seedRoot, connectorType.getDefinitionFileName());
    final JsonNode seedDefinitionsJson = Yamls.deserialize(seedDefinitionsYaml);
    final List<String> seedDefinitionsDockerImages = MoreIterators.toList(seedDefinitionsJson.elements()).stream()
        .map(json -> String.format("%s:%s", json.get(DOCKER_REPOSITORY_FIELD).asText(), json.get(DOCKER_IMAGE_TAG_FIELD).asText()))
        .collect(Collectors.toList());

    final String seedSpecsYaml = IOs.readFile(seedRoot, connectorType.getSpecFileName());
    final JsonNode seedSpecsJson = Yamls.deserialize(seedSpecsYaml);
    final Map<String, DockerImageSpec> currentSeedImageToSpec = MoreIterators.toList(seedSpecsJson.elements()).stream()
        .collect(Collectors.toMap(
            json -> json.get(DockerImageSpec.DOCKER_IMAGE_FIELD).asText(),
            json -> new DockerImageSpec(
                json.get(DockerImageSpec.DOCKER_IMAGE_FIELD).asText(),
                Jsons.object(json.get(DockerImageSpec.SPEC_FIELD), ConnectorSpecification.class))));

    final List<DockerImageSpec> newSeedImageSpecs = seedDefinitionsDockerImages.stream()
        .map(dockerImage -> currentSeedImageToSpec.containsKey(dockerImage) ? currentSeedImageToSpec.get(dockerImage) : fetchSpecFromGCS(dockerImage))
        .collect(Collectors.toList());

    final String outputString = String.format("# This file is generated by %s.\n", this.getClass().getName())
        + "# Do NOT edit this file directly. See generator class for more details.\n"
        + Yamls.serialize(newSeedImageSpecs);
    final Path outputPath = IOs.writeFile(seedRoot.resolve(connectorType.getSpecFileName()), outputString);

    LOGGER.info("Finished updating {}", outputPath);
  }

//  private JsonNode readYaml(Path root, String fileName, String)

  private DockerImageSpec fetchSpecFromGCS(final String dockerImage) {
    LOGGER.info("Seeded spec not found for docker image {} - fetching from GCS bucket {}...", dockerImage, SPEC_BUCKET_NAME);
    final ConnectorSpecification spec = GcsBucketSpecFetcher.attemptFetch(getStorage(), SPEC_BUCKET_NAME, dockerImage)
        .orElseThrow(() -> new RuntimeException(String.format(
            "Failed to fetch valid spec file for docker image %s from GCS bucket %s",
            dockerImage,
            SPEC_BUCKET_NAME)));
    return new DockerImageSpec(dockerImage, spec);
  }

  private static CommandLine parse(final String[] args) {
    final CommandLineParser parser = new DefaultParser();
    final HelpFormatter helpFormatter = new HelpFormatter();

    try {
      return parser.parse(OPTIONS, args);
    } catch (final ParseException e) {
      helpFormatter.printHelp("", OPTIONS);
      throw new IllegalArgumentException(e);
    }
  }

  @JsonPropertyOrder({
      "dockerImage",
      "spec"
  })
  static class DockerImageSpec implements Serializable {

    private static final String DOCKER_IMAGE_FIELD = "dockerImage";
    private static final String SPEC_FIELD = "spec";

    @JsonProperty(DOCKER_IMAGE_FIELD)
    private final String dockerImage;
    @JsonProperty(SPEC_FIELD)
    private final ConnectorSpecification spec;

    DockerImageSpec(final String dockerImage, final ConnectorSpecification spec) {
      this.dockerImage = dockerImage;
      this.spec = spec;
    }
  }
}