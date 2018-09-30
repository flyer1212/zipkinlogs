package hello;

import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

public class SparkOnYarnApp {
    public static void executor() {
        System.out.println("==============spark sql ====");
        SparkSession spark = SparkSession.builder().appName("java spark sql").master("yarn").getOrCreate();
        // sparkSql(spark);
        //  traceEncoder(spark);
        invocation(spark);
        spark.close();
    }

    private static void invocation(SparkSession spark) {
        System.out.println("================ create table  =======================");

        String sql = "create table if not exists invocation_2(invocation_id string , trace_id string, session_id string, req_duration string, req_service string , req_api string, req_params string, exec_duration string, exec_logs string, res_status_code string, res_status_desc string, res_exception string, res_body string, res_duration string, is_error string)";
        spark.sql(sql);

        //  spark.sql("insert into invocation_1 values('12','21','dwe','edew','23','fdfd','5454','323','dfdf','fdfdfd','fdfd','54','ewew')");

//        Dataset<Row> results2 = spark.sql("select * from invocation_2");
//        results2.show();
        Dataset<Row> df = spark.read().option("header", "true").option("inferSchema", true).csv("hdfs://10.141.211.173:8020/user/admin/traces_anno.csv");
        df.printSchema();
        System.out.println("--------------spark sql --------------");
        df.createOrReplaceTempView("trace_anno");

        spark.sql("insert into table  invocation_2 select 0, trace_id, 0, span_duration , anno_cs_servicename, span_name, 0, anno_sr_timestamp -anno_cs_timestamp, 0, res_status_code,res_status_desc,res_exception, 0, anno_ss_timestamp - anno_cr_timestamp, 0 from trace_anno");
        System.out.println("================ create table  end=======================");


        Dataset<Row> results2 = spark.sql("select * from invocation_2");
        // peopleDF.select("name", "age").write().format("parquet").save("namesAndAges.parquet");
        results2.coalesce(1).write().format("com.databricks.spark.csv").save("/user/admin/invocation2.csv");

    }


    private static void createTrace(SparkSession spark){
        Dataset<Row> df = spark.read().option("header", "true").option("inferSchema", true).csv("hdfs://10.141.211.173:8020/user/admin/traces_anno.csv");
        df.show();
        df.createOrReplaceTempView("trace_anno");
        // trace_id

        // entry_service   ---- if cs != null  select  anno_cs_servicename  else if sr != null  select anno_sr_servicename
        // entry_api    ---- span_name
        // entry_timestamp  ---- span_timestamp
//        "select trace_id ";

        Dataset<Row> sqlDF = spark.sql("select trace_id, span_name ,parent_id  from trace_anno where parent_id != '' ");
        sqlDF.show();


    }




    private static void traceEncoder(SparkSession spark) {
        System.out.println("=================invocation----------------");
        Dataset<Row> results = spark.sql("select * from invocation");
        results.show();

        Dataset<Row> results2 = spark.sql("show tables");
        results2.show();

    }

    private static void sparkSql(SparkSession spark) {

        Dataset<Row> df = spark.read().option("header", "true").option("inferSchema", true).csv("hdfs://10.141.211.173:8020/user/admin/traces_anno22.csv");
        df.show();
        System.out.println("----------------schema----------------");
        df.printSchema();

        System.out.println("----------------trace_id----------------");
        df.select("trace_id").show();

        System.out.println("--------------spark sql --------------");
        df.createOrReplaceTempView("trace_anno");


        Dataset<Row> sqlDF2 = spark.sql("select trace_id, span_id  ,parent_id  from trace_anno where parent_id != '' ");
        sqlDF2.show();

        Dataset<Row> tempDF = spark.sql("Select trace_id, span_id from trace_anno where anno_cs != '' AND  anno_sr != ''");
        tempDF.show();
    }
}
