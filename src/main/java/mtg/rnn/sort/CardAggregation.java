package mtg.rnn.sort;

import com.mongodb.DBObject;
import mtg.rnn.sort.card.Card;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Random;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;

@Repository
public class CardAggregation {

    @Autowired
    MongoTemplate mongoTemplate;

    // we have to implement our own sample aggregation operation because apparently
    // spring-data-mongodb didn't have it implemented until spring 2017, and the
    // spring-boot-starter-mongodb version we're using does't have it
    // and I don't know what version , if any, would have it
    private class SampleOperation implements AggregationOperation {
        private DBObject operation;

        public SampleOperation (DBObject operation) {
            this.operation = operation;
        }

        @Override
        public DBObject toDBObject(AggregationOperationContext context) {
            return context.getMappedObject(operation);
        }
    }

//    public List<Card> getUnsorted() {
//        TypedAggregation<Card> agg = newAggregation(
//                Card.class,
//                match(Criteria.where("tags").exists(false)),
//                new SampleOperation(
//                        new BasicDBObject("$sample", new BasicDBObject("size", 1))
//                )
//        );
//
//        AggregationResults<Card> cardResults = mongoTemplate.aggregate(agg, Card.class);
//        List<Card> result = cardResults.getMappedResults();
//
//        return result;
//    }

    public Card getUnsorted() {
        TypedAggregation<Card> agg = newAggregation(
                Card.class,
                match(Criteria.where("tags").exists(false))
        );

        // this is a stupid hack because I can't get sample to work
        AggregationResults<Card> cardResults = mongoTemplate.aggregate(agg, Card.class);
        List<Card> cardList = cardResults.getMappedResults();
        return cardList.get(new Random().nextInt(cardList.size()));
    }
}
