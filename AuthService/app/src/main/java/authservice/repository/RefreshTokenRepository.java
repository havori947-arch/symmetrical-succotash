package authservice.repository;

import authservice.entities.RefreshToken;
import authservice.entities.UserInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends CrudRepository<RefreshToken, String>
{
    Optional<RefreshToken> findByToken(String token);

    boolean existsByUserInfo(UserInfo userInfo);

    RefreshToken findByUserInfo(UserInfo userInfo);


}
