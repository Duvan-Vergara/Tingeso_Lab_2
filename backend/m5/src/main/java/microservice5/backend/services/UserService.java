package microservice5.backend.services;

import microservice5.backend.entities.UserEntity;
import microservice5.backend.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    public List<UserEntity> getUsers(){
        return new ArrayList<>(userRepository.findAll());
    }

    public UserEntity saveUser(UserEntity user){
        return userRepository.save(user);
    }

    public UserEntity getUserById(Long id){
        return userRepository.findById(id).get();
    }

    public boolean deleteUser(Long id) throws Exception {
        try{
            userRepository.deleteById(id);
            return true;
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }
}