package org.example.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Trainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "training_type_id")
    private TrainingType specialization;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToMany(mappedBy = "trainerList")
    private List<Trainee> traineeList = new ArrayList<>();

    @OneToMany(mappedBy = "trainer", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Training> trainingList = new ArrayList<>();

    public String getUsername() {
        return this.getUser().getUsername();
    }

    public String getPassword() {
        return this.getUser().getPassword();
    }

    public void setUsername(String username) {
        this.getUser().setUsername(username);
    }

    public void setPassword(String password) {
        this.getUser().setPassword(password);
    }
}
