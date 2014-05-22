package dao;

import entity.Bid;
import mybatis.BidMapper;
import org.apache.ibatis.session.SqlSession;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * @author Evgenia
 */

@Singleton
public class BidDAO {
    private SqlSessionFactoryHolder sfh;

    public List<Bid> getBidsByProduct(int productId) {
        try (SqlSession session = sfh.getSessionFactory().openSession()) {
            return session.getMapper(BidMapper.class).getBidsByProduct(productId);
        }
    }

    public void create(Bid bid) {
        try (SqlSession session = sfh.getSessionFactory().openSession()) {
            session.getMapper(BidMapper.class).insert(bid);
        }
    }

    @Inject
    public void setSessionFactoryHolder(SqlSessionFactoryHolder sfh) {
        this.sfh = sfh;
    }
}
