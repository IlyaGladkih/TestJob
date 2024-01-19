import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

public class CrptApi {
    private final int limit;
    private final long duration;

    private ObjectMapper mapper;
    private final ConcurrentLinkedQueue<HttpRequest> requestQueue;

    public CrptApi(TimeUnit timeUnit, int requestLimit){
        this.limit = requestLimit;
        duration = timeUnit.toMillis(1);
        requestQueue = new ConcurrentLinkedQueue<>();
        mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false);
        runOtherThread();
    }

    private void runOtherThread() {
        new Thread(() -> {
            HttpClient client = HttpClient.newHttpClient();
            while (true){
                for(int i = 0;i<limit;i++){
                    if(!requestQueue.isEmpty()){
                        HttpRequest request = requestQueue.poll();
                        System.out.println(request);
                        client.sendAsync(request, HttpResponse.BodyHandlers.ofString());
                    }
                }

                try {
                    Thread.sleep(duration);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }).start();
    }

    public void sendRequest(Document document, String subscribe){
        String json = getJSON(document);
        HttpRequest request = HttpRequest.newBuilder()
                .header("subscribe", subscribe)
                .uri(URI.create("https://ismp.crpt.ru/api/v3/lk/documents/create"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        System.out.println(json);
        requestQueue.add(request);
    }

    private String getJSON(Document document) {
        String requestBody = "";
        try {
            requestBody = mapper.writeValueAsString(document);
        } catch (JsonProcessingException e) {
            System.out.println(e);
        }
        return requestBody;
    }


    public static class Document{
        private DocumentDescription description;
        private String doc_id;
        private String doc_status;
        private String doc_type;
        private boolean importRequest;
        private String ownerInn;
        private String participant_inn;
        private String producer_inn;
        private LocalDate production_date;
        private String production_type;
        List<Product> products;
        private LocalDate reg_date;
        private String reg_number;

        public static DocumentBuilder builder(){
            return new DocumentBuilder();
        }

        public DocumentDescription getDescription() {
            return description;
        }

        public void setDescription(DocumentDescription description) {
            this.description = description;
        }

        public String getDoc_id() {
            return doc_id;
        }

        public void setDoc_id(String doc_id) {
            this.doc_id = doc_id;
        }

        public String getDoc_status() {
            return doc_status;
        }

        public void setDoc_status(String doc_status) {
            this.doc_status = doc_status;
        }

        public String getDoc_type() {
            return doc_type;
        }

        public void setDoc_type(String doc_type) {
            this.doc_type = doc_type;
        }

        public boolean isImportRequest() {
            return importRequest;
        }

        public void setImportRequest(boolean importRequest) {
            this.importRequest = importRequest;
        }

        public String getOwnerInn() {
            return ownerInn;
        }

        public void setOwnerInn(String ownerInn) {
            this.ownerInn = ownerInn;
        }

        public String getParticipant_inn() {
            return participant_inn;
        }

        public void setParticipant_inn(String participant_inn) {
            this.participant_inn = participant_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public LocalDate getProduction_date() {
            return production_date;
        }

        public void setProduction_date(LocalDate production_date) {
            this.production_date = production_date;
        }

        public String getProduction_type() {
            return production_type;
        }

        public void setProduction_type(String production_type) {
            this.production_type = production_type;
        }

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }

        public LocalDate getReg_date() {
            return reg_date;
        }

        public void setReg_date(LocalDate reg_date) {
            this.reg_date = reg_date;
        }

        public String getReg_number() {
            return reg_number;
        }

        public void setReg_number(String reg_number) {
            this.reg_number = reg_number;
        }
    }

    public static class DocumentBuilder{
        private Document document;

        public DocumentBuilder() {
            this.document = new Document();
        }

        public DocumentBuilder setDoc_id(String doc_id){
            document.setDoc_id(doc_id);
            return this;
        }

        public DocumentBuilder setDoc_status(String doc_status){
            document.setDoc_status(doc_status);
            return this;
        }

        public DocumentBuilder setDoc_type(String doc_type){
            document.setDoc_type(doc_type);
            return this;
        }

        public DocumentBuilder setImportRequest(boolean importRequest){
            document.setImportRequest(importRequest);
            return this;
        }

        public DocumentBuilder setOwnerInn(String ownerInn){
            document.setOwnerInn(ownerInn);
            return this;
        }

        public DocumentBuilder setParticipant_inn(String participant_inn){
            document.setParticipant_inn(participant_inn);
            return this;
        }

        public DocumentBuilder setProducer_inn(String producer_inn){
            document.setProducer_inn(producer_inn);
            return this;
        }

        public DocumentBuilder setProduction_date(LocalDate production_date){
            document.setProduction_date(production_date);
            return this;
        }

        public DocumentBuilder setProduction_type(String production_type){
            document.setProduction_type(production_type);
            return this;
        }

        public DocumentBuilder setReg_date(LocalDate reg_date){
            document.setReg_date(reg_date);
            return this;
        }

        public DocumentBuilder setReg_number(String reg_number){
            document.setReg_number(reg_number);
            return this;
        }

        public DocumentBuilder setDescription(DocumentDescription description){
            document.setDescription(description);
            return this;
        }

        public DocumentBuilder setProducts(List<Product> products){
            document.setProducts(products);
            return this;
        }

        public Document build(){
            return this.document;
        }
    }

    public static class DocumentDescription{
        private String participantInn;

        public DocumentDescription(String participantInn) {
            this.participantInn = participantInn;
        }

        public String getParticipantInn() {
            return participantInn;
        }

        public void setParticipantInn(String participantInn) {
            this.participantInn = participantInn;
        }
    }

    public static class Product{
        private String certificate_document;
        private LocalDate certificate_document_date;
        private String certificate_document_number;
        private String owner_inn;
        private String producer_inn;
        private LocalDate production_date;
        private String tnved_code;
        private String uit_code;
        private String uitu_code;

        public static ProductBuilder builder(){
            return new ProductBuilder();
        }

        public String getCertificate_document() {
            return certificate_document;
        }

        public void setCertificate_document(String certificate_document) {
            this.certificate_document = certificate_document;
        }

        public LocalDate getCertificate_document_date() {
            return certificate_document_date;
        }

        public void setCertificate_document_date(LocalDate certificate_document_date) {
            this.certificate_document_date = certificate_document_date;
        }

        public String getCertificate_document_number() {
            return certificate_document_number;
        }

        public void setCertificate_document_number(String certificate_document_number) {
            this.certificate_document_number = certificate_document_number;
        }

        public String getOwner_inn() {
            return owner_inn;
        }

        public void setOwner_inn(String owner_inn) {
            this.owner_inn = owner_inn;
        }

        public String getProducer_inn() {
            return producer_inn;
        }

        public void setProducer_inn(String producer_inn) {
            this.producer_inn = producer_inn;
        }

        public LocalDate getProduction_date() {
            return production_date;
        }

        public void setProduction_date(LocalDate production_date) {
            this.production_date = production_date;
        }

        public String getTnved_code() {
            return tnved_code;
        }

        public void setTnved_code(String tnved_code) {
            this.tnved_code = tnved_code;
        }

        public String getUit_code() {
            return uit_code;
        }

        public void setUit_code(String uit_code) {
            this.uit_code = uit_code;
        }

        public String getUitu_code() {
            return uitu_code;
        }

        public void setUitu_code(String uitu_code) {
            this.uitu_code = uitu_code;
        }
    }

    public static class ProductBuilder{
        private Product product;

        public ProductBuilder() {
            this.product = new Product();
        }

        public ProductBuilder setCertificate_document(String certificate_document){
            product.setCertificate_document(certificate_document);
            return this;
        }

        public ProductBuilder setCertificate_document_date(LocalDate certificate_document_date){
            product.setCertificate_document_date(certificate_document_date);
            return this;
        }

        public ProductBuilder setCertificate_document_number(String certificate_document_number){
            product.setCertificate_document_number(certificate_document_number);
            return this;
        }

        public ProductBuilder setOwner_inn(String owner_inn){
            product.setOwner_inn(owner_inn);
            return this;
        }

        public ProductBuilder setProducer_inn(String producer_inn){
            product.setProducer_inn(producer_inn);
            return this;
        }

        public ProductBuilder setProduction_date(LocalDate production_date){
            product.setProduction_date(production_date);
            return this;
        }

        public ProductBuilder setTnved_code(String tnved_code){
            product.setTnved_code(tnved_code);
            return this;
        }

        public ProductBuilder setUit_code(String uit_code){
            product.setUit_code(uit_code);
            return this;
        }

        public ProductBuilder setUitu_code(String uitu_code){
            product.setUitu_code(uitu_code);
            return this;
        }

        public Product build(){
            return this.product;
        }
    }
}
