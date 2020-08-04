package plus.gifu.data.leaf.segment.dao;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import plus.gifu.data.leaf.segment.mapper.KeySequenceMapper;
import plus.gifu.data.leaf.segment.model.KeySequence;

import javax.sql.DataSource;

public class KeySequenceDao {

    private SqlSessionFactory sqlSessionFactory;

    public KeySequenceDao(DataSource dataSource) {
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("leaf", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(KeySequenceMapper.class);
        sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
    }

    public KeySequence get(String key) {
        try (SqlSession sqlSession = sqlSessionFactory.openSession()){
            return sqlSession.selectOne("plus.gifu.data.leaf.segment.mapper.KeySequenceMapper.get", key);
        }
    }

}
