package tn.esprit.foyer.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tn.esprit.foyer.dto.RoomAllocationDTO;
import tn.esprit.foyer.entities.Chambre;
import tn.esprit.foyer.entities.Etudiant;
import tn.esprit.foyer.entities.Tache;
import tn.esprit.foyer.entities.TypeChambre;
import tn.esprit.foyer.repository.ChambreRepository;
import tn.esprit.foyer.repository.EtudiantRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RoomAllocationServiceTest {

    @Mock
    private ChambreRepository chambreRepository;
    
    @Mock
    private EtudiantRepository etudiantRepository;
    
    private RoomAllocationService roomAllocationService;
    
    private List<Chambre> mockChambres;
    private List<Etudiant> mockEtudiants;

    @BeforeEach
    void setUp() {
        // Create test chambers
        mockChambres = new ArrayList<>();
        
        Chambre chambre1 = Chambre.builder().idChambre(1L).numeroChambre(101L).typeC(TypeChambre.DOUBLE).build();
        Chambre chambre2 = Chambre.builder().idChambre(2L).numeroChambre(102L).typeC(TypeChambre.SIMPLE).build();
        mockChambres.addAll(Arrays.asList(chambre1, chambre2));
        
        // Create test students
        mockEtudiants = new ArrayList<>();
        
        Etudiant e1 = new Etudiant();
        e1.setNomEt("Ali");
        e1.setPrenomEt("Ben Salah");
        e1.setEcole("ESPRIT");
        List<Tache> tachesE1 = new ArrayList<>();
        Tache tache1 = new Tache();
        tache1.setDateTache(LocalDate.now());
        tache1.setDuree(5);
        tache1.setEtudiant(e1);
        tachesE1.add(tache1);
        e1.setTaches(tachesE1);
        
        Etudiant e2 = new Etudiant();
        e2.setNomEt("Sami");
        e2.setPrenomEt("Trabelsi");
        e2.setEcole("ENIT");
        List<Tache> tachesE2 = new ArrayList<>();
        Tache tache2 = new Tache();
        tache2.setDateTache(LocalDate.now());
        tache2.setDuree(3);
        tache2.setEtudiant(e2);
        tachesE2.add(tache2);
        e2.setTaches(tachesE2);
        
        Etudiant e3 = new Etudiant();
        e3.setNomEt("Yasmine");
        e3.setPrenomEt("Mejri");
        e3.setEcole("IHEC");
        List<Tache> tachesE3 = new ArrayList<>();
        Tache tache3 = new Tache();
        tache3.setDateTache(LocalDate.now());
        tache3.setDuree(7);
        tache3.setEtudiant(e3);
        tachesE3.add(tache3);
        e3.setTaches(tachesE3);
        
        mockEtudiants.addAll(Arrays.asList(e1, e2, e3));
        
        roomAllocationService = new RoomAllocationService(chambreRepository, etudiantRepository);
    }

    @Test
    @DisplayName("Should allocate students across rooms balancing workload")
    void testRoomAllocationWorkloadBalance() {
        // Setup mock repository behavior
        when(chambreRepository.findAll()).thenReturn(mockChambres);
        when(etudiantRepository.findAll()).thenReturn(mockEtudiants);
        
        // Execute the method
        List<RoomAllocationDTO> allocations = roomAllocationService.allocateRooms();

        // Verify
        assertEquals(2, allocations.size(), "Should have 2 rooms");

        int totalStudents = allocations.stream().mapToInt(r -> r.getOccupants().size()).sum();
        assertEquals(3, totalStudents, "All 3 students should be allocated");

        // Print debug information
        System.out.println("Room allocations:");
        for (int i = 0; i < allocations.size(); i++) {
            RoomAllocationDTO room = allocations.get(i);
            System.out.println("Room " + (i+1) + " - Occupants: " + room.getOccupants().size() + ", Total load: " + room.getTotalLoad());
            room.getOccupants().forEach(student -> 
                System.out.println("  - " + student.getEtudiant().getNomEt() + " (Workload: " + student.getWorkload() + ")"));
        }

        // Check that all students are assigned
        List<String> names = allocations.stream()
                .flatMap(r -> r.getOccupants().stream())
                .map(o -> o.getEtudiant().getNomEt())
                .toList();

        assertTrue(names.contains("Ali"), "Ali should be allocated");
        assertTrue(names.contains("Sami"), "Sami should be allocated");
        assertTrue(names.contains("Yasmine"), "Yasmine should be allocated");
        
        // Verify repository methods were called
        verify(chambreRepository, times(1)).findAll();
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return empty list if no rooms are available")
    void testNoRoomsAvailable() {
        // Setup mock repository behavior
        when(chambreRepository.findAll()).thenReturn(List.of());
        when(etudiantRepository.findAll()).thenReturn(mockEtudiants);
        
        // Execute the method
        List<RoomAllocationDTO> allocations = roomAllocationService.allocateRooms();

        // Verify
        assertNotNull(allocations, "Result should not be null");
        assertTrue(allocations.isEmpty(), "Result should be empty");
        
        verify(chambreRepository, times(1)).findAll();
        verify(etudiantRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Should return rooms with no occupants if no students exist")
    void testNoStudentsAvailable() {
        when(chambreRepository.findAll()).thenReturn(mockChambres);
        when(etudiantRepository.findAll()).thenReturn(List.of());
        
        List<RoomAllocationDTO> allocations = roomAllocationService.allocateRooms();

        assertNotNull(allocations, "Result should not be null");
        assertEquals(2, allocations.size(), "Should have 2 empty rooms");

        for (RoomAllocationDTO room : allocations) {
            assertEquals(0, room.getOccupants().size(), "Room should have no occupants");
        }
        
        verify(chambreRepository, times(1)).findAll();
        verify(etudiantRepository, times(1)).findAll();
    }
}
