package hello;

import com.google.gson.Gson;
import hello.domain.Annotation;
import hello.domain.NewAnnoation;
import hello.domain.Trace;
import hello.storage.CsvFilePrinter;
import hello.storage.ParquetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Arrays;

@Component
public class KafkaConsumer {

    private static final Logger log = LoggerFactory.getLogger(Application.class);

    /**
     * reader
     */
//    @KafkaListener(topics = {"app_log"} , containerFactory = "batchFactory")
    @KafkaListener(topics = {"app_log"})
    public void consumer(String message) throws IOException {
        System.out.println("[===] KafkaConsumer - consumer");
        System.out.println("==========KafkaConsumer - consumer============");

        Gson gson = new Gson();
        Trace[] traces = gson.fromJson(message, Trace[].class);

        String annoCsvFile = "/parquet/traces_anno.csv";
        String binnoCsvFile = "/parquet/traces_binno.csv";

        CsvFilePrinter annoPrint = new CsvFilePrinter(annoCsvFile, true, true);
        CsvFilePrinter binnoPrint = new CsvFilePrinter(binnoCsvFile, true, false);


        for (int i = 0; i < traces.length; i++) {
            NewAnnoation[] newAnnoations = new NewAnnoation[4];
            newAnnoations[0] = new NewAnnoation();
            newAnnoations[1] = new NewAnnoation();
            newAnnoations[2] = new NewAnnoation();
            newAnnoations[3] = new NewAnnoation();

            for (int j = 0; traces[i].getAnnotations() != null && j < traces[i].getAnnotations().length; j++) {
                Annotation tempAnno = traces[i].getAnnotations()[j];
                if (tempAnno.getValue().equals("cs")) {
                    newAnnoations[0] = new NewAnnoation(tempAnno.getTimestamp(), tempAnno.getValue(), tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(), tempAnno.getEndpoint().getPort());
                }
                if (tempAnno.getValue().equals("cr")) {
                    newAnnoations[1] = new NewAnnoation(tempAnno.getTimestamp(), tempAnno.getValue(), tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(), tempAnno.getEndpoint().getPort());
                }
                if (tempAnno.getValue().equals("sr")) {
                    newAnnoations[2] = new NewAnnoation(tempAnno.getTimestamp(), tempAnno.getValue(), tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(), tempAnno.getEndpoint().getPort());
                }
                if (tempAnno.getValue().equals("ss")) {
                    newAnnoations[3] = new NewAnnoation(tempAnno.getTimestamp(), tempAnno.getValue(), tempAnno.getEndpoint().getServiceName(),
                            tempAnno.getEndpoint().getIpv4(), tempAnno.getEndpoint().getPort());
                }
            }
            String status_code  = "200";
            String status_desc  = "0";
            String res_exception = "0";
            String[] status_codes = {"200","202","203","204","301","302","303","304","305","307","400","401","403","404","405","500","501","502","503","504","505"};

            for (int j = 0; traces[i].getBinaryAnnotations() != null && j < traces[i].getBinaryAnnotations().length; j++) {

                String key = traces[i].getBinaryAnnotations()[j].getKey();
                String value = traces[i].getBinaryAnnotations()[j].getValue();

                if( key!= null && key.contains("status_code") && Arrays.binarySearch(status_codes,value) >= 0){
                    status_code = value;
                }

                if( key != null && key.contains("error")){
                    status_desc = key;
                    res_exception = value;
                }

                //写入 binno csv
                binnoPrint.write(new String[]{
                        traces[i].getId(),
                        traces[i].getBinaryAnnotations()[j].getKey(),
                        traces[i].getBinaryAnnotations()[j].getValue(),
                        traces[i].getBinaryAnnotations()[j].getEndpoint().getServiceName(),
                        traces[i].getBinaryAnnotations()[j].getEndpoint().getIpv4(),
                        "" + traces[i].getBinaryAnnotations()[j].getEndpoint().getPort(),
                });
            }

            //写入 anno csv
            annoPrint.write(new String[]{
                    traces[i].getTraceId(),
                    traces[i].getId(),
                    traces[i].getName(),
                    traces[i].getParentId(),
                    "" + traces[i].getTimestamp(),
                    "" + traces[i].getDuration(),

                    "" + newAnnoations[0].getTimestamp(),
                    newAnnoations[0].getValue(),
                    newAnnoations[0].getServiceName(),
                    newAnnoations[0].getIpv4(),
                    "" + newAnnoations[0].getPort(),

                    "" + newAnnoations[1].getTimestamp(),
                    newAnnoations[1].getValue(),
                    newAnnoations[1].getServiceName(),
                    newAnnoations[1].getIpv4(),
                    "" + newAnnoations[1].getPort(),


                    "" + newAnnoations[2].getTimestamp(),
                    newAnnoations[2].getValue(),
                    newAnnoations[2].getServiceName(),
                    newAnnoations[2].getIpv4(),
                    "" + newAnnoations[2].getPort(),


                    "" + newAnnoations[3].getTimestamp(),
                    newAnnoations[3].getValue(),
                    newAnnoations[3].getServiceName(),
                    newAnnoations[3].getIpv4(),
                    "" + newAnnoations[3].getPort(),
                    status_code,
                    status_desc,
                    res_exception
            });
        }

        log.info("[===] The size of traces: " + traces.length);
        log.info("[===] The TRACE-ID of traces: " + traces[0].getTraceId());
        System.out.println("==========================");
    }

    public void genInvocationCSV() {
        System.out.println("======== generate invocation =======");
    }
}
