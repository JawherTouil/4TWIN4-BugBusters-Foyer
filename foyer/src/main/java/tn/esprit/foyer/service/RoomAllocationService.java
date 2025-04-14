package tn.esprit.foyer.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tn.esprit.foyer.dto.EtudiantWorkloadDTO;
import tn.esprit.foyer.dto.RoomAllocationDTO;
import tn.esprit.foyer.entities.Chambre;
import tn.esprit.foyer.entities.Etudiant;
import tn.esprit.foyer.repository.ChambreRepository;
import tn.esprit.foyer.repository.EtudiantRepository;

import java.util.Comparator;
import java.util.List;


@Service
@AllArgsConstructor
public class RoomAllocationService {
    private final ChambreRepository chambreRepository;
    private final EtudiantRepository etudiantRepository;

    public List<RoomAllocationDTO> allocateRooms() {
        List<Chambre> chambres = chambreRepository.findAll();
        List<Etudiant> etudiants = etudiantRepository.findAll();

        // Convert students to workload DTOs and sort by workload
        List<EtudiantWorkloadDTO> sortedStudents = etudiants.stream()
                .map(EtudiantWorkloadDTO::new)
                .sorted((a, b) -> Integer.compare(b.getWorkload(), a.getWorkload()))
                .toList();

        // Create room allocation objects
        List<RoomAllocationDTO> roomAllocations = chambres.stream()
                .map(RoomAllocationDTO::new)
                .toList();

        // Allocate students to rooms
        for (EtudiantWorkloadDTO student : sortedStudents) {
            roomAllocations.stream()
                    .filter(RoomAllocationDTO::canAddOccupant)
                    .min(Comparator.comparingInt(r -> Math.abs(r.getTotalLoad() - student.getWorkload())))
                    .ifPresent(room -> room.addStudent(student));
        }

        return roomAllocations;
    }
    //test harnas
}
