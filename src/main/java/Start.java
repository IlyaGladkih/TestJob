import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Start {

    public static void main(String[] args) {
        CrptApi crptApi = new CrptApi(TimeUnit.SECONDS, 4);
        ExecutorService service = Executors.newFixedThreadPool(10);
        for(int i = 0;i<100;i++){
            int k = i;
            service.execute(()->{
                crptApi.sendRequest(
                        CrptApi.Document.builder()
                                .setDescription(new CrptApi.DocumentDescription("string"))
                                .setDoc_id("string").setDoc_status("string").setDoc_type("string")
                                .setDoc_type("LP_INTRODUCE_GOODS").setImportRequest(true)
                                .setOwnerInn("string").setProducer_inn("string").setParticipant_inn("string")
                                .setProduction_date(LocalDate.of(2020,1,23))
                                .setProduction_type("string")
                                .setProducts(List.of(CrptApi.Product.builder()
                                                .setCertificate_document("string")
                                                .setCertificate_document_date(LocalDate.of(2020,1,23))
                                                .setCertificate_document_number("string")
                                                .setOwner_inn("string")
                                                .setProducer_inn("string")
                                                .setProduction_date(LocalDate.of(2020,1,23))
                                                .setTnved_code("string")
                                                .setUit_code("string")
                                                .setUitu_code("string")
                                        .build()))
                                .setReg_date(LocalDate.of(2020,1,23))
                                .setReg_number("string")
                                .build()
                        ,String.valueOf(k));
            });
        }
        service.shutdown();
    }
}
