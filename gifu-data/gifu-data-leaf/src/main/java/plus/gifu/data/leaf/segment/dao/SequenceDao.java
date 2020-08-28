package plus.gifu.data.leaf.segment.dao;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import plus.gifu.data.leaf.segment.mapper.SequenceMapper;
import plus.gifu.data.leaf.segment.model.Sequence;

import javax.sql.DataSource;

public class SequenceDao {

    private final SqlSessionFactory sqlSessionFactory;

    public SequenceDao(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("leaf", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(SequenceMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    public Sequence get(String key) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            return sqlSession.selectOne("plus.gifu.data.leaf.segment.mapper.SequenceMapper.get", key);
        }
    }

    public int insert(Sequence sequence) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            int num = sqlSession.insert("plus.gifu.data.leaf.segment.mapper.SequenceMapper.insert", sequence);
            sqlSession.commit();
            return num;
        }
    }

    public int updateMaxId(Sequence sequence) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            int num = sqlSession.update("plus.gifu.data.leaf.segment.mapper.SequenceMapper.updateMaxId", sequence);
            sqlSession.commit();
            return num;
        }
    }

}
