package yuliya.akkuzhyna.persistence;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import yuliya.akkuzhyna.service.Frame;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import static yuliya.akkuzhyna.utils.Constants.NUM_FRAMES;

@Getter
@Entity
@Table(name = "player", schema = "bowling_club")
@JsonIgnoreProperties(value = { "id" })
@NoArgsConstructor
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private  Long id;

    private String name;
    @Transient
    private Queue<Frame> framesQueue = new LinkedList<>();
    @Transient//for unit tests only
    private final List<Integer> finalScores = new ArrayList<>(NUM_FRAMES);

    public Player(long testId, String testName) {
        this.id = testId;
        this.name = testName;
    }

    public int getFinalRollsToDo() {
        return framesQueue.stream().mapToInt(Frame::getRollsToDo).sum();
    }

    public void addToFinalScoresLog(int i) {
        finalScores.add(i);
    }
}