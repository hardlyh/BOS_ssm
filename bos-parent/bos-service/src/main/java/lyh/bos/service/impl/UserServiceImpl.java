package lyh.bos.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lyh.bos.dao.IUserDao;
import lyh.bos.domain.User;
import lyh.bos.service.IUserService;
import lyh.bos.utils.MD5Utils;

@Service
@Transactional
public class UserServiceImpl implements IUserService{
    
    @Autowired
    private IUserDao userDao;
    

    public IUserDao getUserDao() {
        return userDao;
    }


    public void setUserDao(IUserDao userDao) {
        this.userDao = userDao;
    }


    @Override
    public User login(User user) {
        String password =  MD5Utils.md5(user.getPassword());
       
        return userDao.findUserByUsernameAndPassword(user.getUsername(),password);
        
    }

}