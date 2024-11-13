package yuliya.akkuzhyna.service;


import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import yuliya.akkuzhyna.dto.PlayerDto;
import yuliya.akkuzhyna.exception.PlayerNotFoundException;
import yuliya.akkuzhyna.persistence.Player;
import yuliya.akkuzhyna.persistence.PlayerRepository;

import java.util.List;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@AllArgsConstructor
public class PlayerService {

    private PlayerRepository playerRepository;

    @Transactional(readOnly = true)
    public List<PlayerDto> findAll(){
        return playerRepository.findAll().stream().map(this::mapToDto).toList();
    }

    @Transactional(readOnly = true)
    public Player findPlayer(long id) throws PlayerNotFoundException {
        return playerRepository.findById(id).orElseThrow(()->
                new PlayerNotFoundException("player with id "+id+ " is not in Database"));
    }

    private PlayerDto mapToDto(Player p){
        return new PlayerDto(p.getName());
    }
}
