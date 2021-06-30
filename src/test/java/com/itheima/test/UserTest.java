package com.itheima.test;

import com.itheima.dao.IUserDao;
import com.itheima.domain.User;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

/**
 * @author 黑马程序员
 * @Company http://www.ithiema.com
 *
 * 测试mybatis的crud操作
 */
public class UserTest {

    private InputStream in;
    private SqlSession sqlSession;
    private IUserDao userDao;
    private SqlSessionFactory factory;

    @Before//用于在测试方法执行之前执行
    public void init()throws Exception{
        //1.读取配置文件，生成字节输入流
        in = Resources.getResourceAsStream("SqlMapConfig.xml");
        //2.获取SqlSessionFactory
        factory = new SqlSessionFactoryBuilder().build(in);
        //3.获取SqlSession对象
        sqlSession = factory.openSession(true);//改成true是可以自动提交
        //sqlSession可以清楚缓存
//        sqlSession.clearCache();

        //4.获取dao的代理对象
        userDao = sqlSession.getMapper(IUserDao.class);
    }

    @After//用于在测试方法执行之后执行
    public void destroy()throws Exception{
        //提交事务
//        sqlSession.commit();
        //6.释放资源
        sqlSession.close();
        in.close();
    }

    /**
     * 验证了缓存的存在
     */
    @Test
    public void firstLevelCache(){
        //1,根据id查询用户
        User user1 = userDao.findById(41);
        System.out.println(user1);
        //2,更新用户信息
        user1.setUsername("update user clear cache");
        user1.setAddress("南京");
        userDao.updateUser(user1);
        //3,再次根据id查询用户
        User user2 = userDao.findById(41);
        System.out.println(user2);
        System.out.println(user1==user2);

    }


    /**
     * 验证了缓存的同步,怎么保证缓存数据不变的
     */
    @Test
    public void firstClearCache(){
        User user1 = userDao.findById(41);
        System.out.println(user1);

        sqlSession.close();
        //再次获取
        sqlSession = factory.openSession();
        userDao = sqlSession.getMapper(IUserDao.class);

        User user2 = userDao.findById(41);
        System.out.println(user2);
        System.out.println(user1==user2);

    }


    /**
     * 查询所有
     */
    @Test
    public void testFindAll(){
        List<User> users = userDao.findAll();
        for(User user : users){
            System.out.println("---------每个用户的信息-----------");
            System.out.println(user);
        }
    }



}
