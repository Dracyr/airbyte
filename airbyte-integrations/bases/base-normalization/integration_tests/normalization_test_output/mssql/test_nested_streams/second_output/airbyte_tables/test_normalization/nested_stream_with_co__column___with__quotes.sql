
   
  USE [test_normalization];
  if object_id ('test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp_temp_view"','V') is not null
      begin
      drop view test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp_temp_view"
      end


   
   
  USE [test_normalization];
  if object_id ('test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp"','U') is not null
      begin
      drop table test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp"
      end


   USE [test_normalization];
   EXEC('create view test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp_temp_view" as
    
-- Final base SQL model
select
    _airbyte_partition_hashid,
    currency,
    _airbyte_ab_id,
    _airbyte_emitted_at,
    SYSDATETIME() as _airbyte_normalized_at,
    _airbyte_column___with__quotes_hashid
from "test_normalization"._airbyte_test_normalization."nested_stream_with_co__column___with__quotes_ab3"
-- column___with__quotes at nested_stream_with_complex_columns_resulting_into_long_names/partition/column`_''with"_quotes from "test_normalization".test_normalization."nested_stream_with_co___long_names_partition"
where 1 = 1
    ');

   SELECT * INTO "test_normalization".test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp" FROM
    "test_normalization".test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp_temp_view"

   
   
  USE [test_normalization];
  if object_id ('test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp_temp_view"','V') is not null
      begin
      drop view test_normalization."nested_stream_with_co__column___with__quotes__dbt_tmp_temp_view"
      end

    
   use [test_normalization];
  if EXISTS (
        SELECT * FROM
        sys.indexes WHERE name = 'test_normalization_nested_stream_with_co__column___with__quotes__dbt_tmp_cci'
        AND object_id=object_id('test_normalization_nested_stream_with_co__column___with__quotes__dbt_tmp')
    )
  DROP index test_normalization.nested_stream_with_co__column___with__quotes__dbt_tmp.test_normalization_nested_stream_with_co__column___with__quotes__dbt_tmp_cci
  CREATE CLUSTERED COLUMNSTORE INDEX test_normalization_nested_stream_with_co__column___with__quotes__dbt_tmp_cci
    ON test_normalization.nested_stream_with_co__column___with__quotes__dbt_tmp

   

