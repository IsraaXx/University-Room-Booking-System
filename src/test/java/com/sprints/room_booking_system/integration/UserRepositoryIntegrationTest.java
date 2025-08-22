package com.sprints.room_booking_system.integration;

import com.sprints.room_booking_system.model.Department;
import com.sprints.room_booking_system.model.User;
import com.sprints.room_booking_system.model.UserRole;
import com.sprints.room_booking_system.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class UserRepositoryIntegrationTest extends RepositoryIntegrationTestBase {

    @Autowired
    private UserRepository userRepository;

    @Test
    void testSaveAndFindById() {
        // Given
        Department department = new Department();
        department.setName("Computer Science");
        department = persistAndFlush(department);

        User user = User.builder()
                .name("John Doe")
                .email("john.doe@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(department)
                .build();

        // When
        User savedUser = userRepository.save(user);
        entityManager.flush();
        Optional<User> foundUser = userRepository.findById(savedUser.getId());

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("John Doe");
        assertThat(foundUser.get().getEmail()).isEqualTo("john.doe@university.edu");
        assertThat(foundUser.get().getRole()).isEqualTo(UserRole.STUDENT);
    }

    @Test
    void testFindByEmail() {
        // Given
        Department department = new Department();
        department.setName("Physics");
        department = persistAndFlush(department);

        User user = User.builder()
                .name("Jane Smith")
                .email("jane.smith@university.edu")
                .password("password")
                .role(UserRole.FACULTY)
                .isActive(true)
                .department(department)
                .build();
        userRepository.save(user);
        entityManager.flush();

        // When
        Optional<User> foundUser = userRepository.findByEmail("jane.smith@university.edu");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Jane Smith");
        assertThat(foundUser.get().getRole()).isEqualTo(UserRole.FACULTY);
    }

    @Test
    void testExistsByEmail() {
        // Given
        Department department = new Department();
        department.setName("Mathematics");
        department = persistAndFlush(department);

        User user = User.builder()
                .name("Bob Wilson")
                .email("bob.wilson@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(department)
                .build();
        userRepository.save(user);
        entityManager.flush();

        // When & Then
        assertThat(userRepository.existsByEmail("bob.wilson@university.edu")).isTrue();
        assertThat(userRepository.existsByEmail("nonexistent@university.edu")).isFalse();
    }

    @Test
    void testFindActiveUsers() {
        // Given
        Department department = new Department();
        department.setName("Engineering");
        department = persistAndFlush(department);

        User activeUser = User.builder()
                .name("Active User")
                .email("active@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(department)
                .build();

        User inactiveUser = User.builder()
                .name("Inactive User")
                .email("inactive@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(false)
                .department(department)
                .build();

        userRepository.save(activeUser);
        userRepository.save(inactiveUser);
        entityManager.flush();

        // When
        List<User> activeUsers = userRepository.findByIsActiveTrue();

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getName()).isEqualTo("Active User");
        assertThat(activeUsers.get(0).getIsActive()).isTrue();
    }

    @Test
    void testFindByRole() {
        // Given
        Department department = new Department();
        department.setName("History");
        department = persistAndFlush(department);

        User student = User.builder()
                .name("Student User")
                .email("student@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(department)
                .build();

        User faculty = User.builder()
                .name("Faculty User")
                .email("faculty@university.edu")
                .password("password")
                .role(UserRole.FACULTY)
                .isActive(true)
                .department(department)
                .build();

        userRepository.save(student);
        userRepository.save(faculty);
        entityManager.flush();

        // When
        List<User> students = userRepository.findByRole(UserRole.STUDENT);
        List<User> faculties = userRepository.findByRole(UserRole.FACULTY);

        // Then
        assertThat(students).hasSize(1);
        assertThat(students.get(0).getName()).isEqualTo("Student User");
        assertThat(faculties).hasSize(1);
        assertThat(faculties.get(0).getName()).isEqualTo("Faculty User");
    }

    @Test
    void testFindByDepartmentId() {
        // Given
        Department csDepartment = new Department();
        csDepartment.setName("Computer Science");
        csDepartment = persistAndFlush(csDepartment);

        Department physicsDepartment = new Department();
        physicsDepartment.setName("Physics");
        physicsDepartment = persistAndFlush(physicsDepartment);

        User csUser = User.builder()
                .name("CS User")
                .email("cs@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(csDepartment)
                .build();

        User physicsUser = User.builder()
                .name("Physics User")
                .email("physics@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(physicsDepartment)
                .build();

        userRepository.save(csUser);
        userRepository.save(physicsUser);
        entityManager.flush();

        // When
        List<User> csUsers = userRepository.findByDepartmentId(csDepartment.getId());

        // Then
        assertThat(csUsers).hasSize(1);
        assertThat(csUsers.get(0).getName()).isEqualTo("CS User");
        assertThat(csUsers.get(0).getDepartment().getName()).isEqualTo("Computer Science");
    }

    @Test
    void testFindByRoleAndDepartment() {
        // Given
        Department department = new Department();
        department.setName("Chemistry");
        department = persistAndFlush(department);

        User student1 = User.builder()
                .name("Student 1")
                .email("student1@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(department)
                .build();

        User student2 = User.builder()
                .name("Student 2")
                .email("student2@university.edu")
                .password("password")
                .role(UserRole.STUDENT)
                .isActive(true)
                .department(department)
                .build();

        User faculty = User.builder()
                .name("Faculty Member")
                .email("faculty@university.edu")
                .password("password")
                .role(UserRole.FACULTY)
                .isActive(true)
                .department(department)
                .build();

        userRepository.save(student1);
        userRepository.save(student2);
        userRepository.save(faculty);
        entityManager.flush();

        // When
        List<User> students = userRepository.findByRoleAndDepartment(UserRole.STUDENT, department.getId());

        // Then
        assertThat(students).hasSize(2);
        assertThat(students).extracting(User::getRole).containsOnly(UserRole.STUDENT);
        assertThat(students).extracting(User::getDepartment).containsOnly(department);
    }
}
