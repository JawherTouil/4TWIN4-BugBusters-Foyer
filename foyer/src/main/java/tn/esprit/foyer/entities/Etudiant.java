package tn.esprit.foyer.entities;


import jakarta.persistence.*;
import lombok.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@RequiredArgsConstructor
public class Etudiant implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idEtudiant; // Clé primaire
    @NonNull
    private String nomEt;
    @NonNull
     String prenomEt;
     Long cin;
     String ecole;
     LocalDate dateNaissance;










    @OneToMany(mappedBy = "etudiant", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Tache> taches = new ArrayList<>();
    Float montantInscription;
    @Enumerated(EnumType.STRING)
    TypeEtudiant typeEtudiant;

    @ManyToMany(mappedBy = "etudiants",fetch = FetchType.EAGER)
    private List<Reservation> reservations;

    @OneToOne
    Tache tache;
    public Etudiant(String nomEt, String prenomEt, String ecole) {
        this.nomEt = nomEt;
        this.prenomEt = prenomEt;
        this.ecole = ecole;
    }





}

