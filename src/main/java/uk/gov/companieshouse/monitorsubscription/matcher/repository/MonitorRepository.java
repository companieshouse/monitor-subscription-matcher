package uk.gov.companieshouse.monitorsubscription.matcher.repository;

import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.gov.companieshouse.monitorsubscription.matcher.repository.model.MonitorQueryDocument;

public interface MonitorRepository extends MongoRepository<MonitorQueryDocument, String> {

    List<MonitorQueryDocument> findByCompanyNumberAndIsActive(String companyNumber, Boolean isActive);

}
