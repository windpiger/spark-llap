package com.hortonworks.spark.sql.hive.llap;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.apache.spark.sql.SaveMode;
import org.apache.spark.sql.sources.v2.DataSourceOptions;
import org.apache.spark.sql.sources.v2.DataSourceV2;
import org.apache.spark.sql.sources.v2.WriteSupport;
import org.apache.spark.sql.sources.v2.writer.DataSourceWriter;
import org.apache.spark.sql.types.StructType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HiveStreamingDataSource implements DataSourceV2, WriteSupport {
  private static final long DEFAULT_COMMIT_INTERVAL_ROWS = 10000;
  private static Logger LOG = LoggerFactory.getLogger(HiveStreamingDataSource.class);

  @Override
  public Optional<DataSourceWriter> createWriter(final String jobId, final StructType schema, final SaveMode mode,
    final DataSourceOptions options) {
    return Optional.of(createDataSourceWriter(jobId, schema, options));
  }

  private HiveStreamingDataSourceWriter createDataSourceWriter(final String id, final StructType schema,
    final DataSourceOptions options) {
    String dbName = options.get("database").orElse("default");
    String tableName = options.get("table").orElse(null);
    String partition = options.get("partition").orElse(null);
    List<String> partitionValues = partition == null ? null : Arrays.asList(partition.split(","));
    String metastoreUri = options.get("metastoreUri").orElse("thrift://localhost:9083");
    String commitIntervalRows = options.get("commitIntervalRows").orElse("" + DEFAULT_COMMIT_INTERVAL_ROWS);
    long commitInterval = Long.parseLong(commitIntervalRows);
    LOG.info("OPTIONS - database: {} table: {} partition: {} commitIntervalRows: {} metastoreUri: {}", dbName,
      tableName, partition, commitInterval, metastoreUri);
    return new HiveStreamingDataSourceWriter(id, schema, commitInterval, dbName, tableName,
      partitionValues, metastoreUri);
  }

}

