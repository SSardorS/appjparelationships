package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    AddressRepository addressRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    SubjectRepository subjectRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAll(pageable);
        return studentPage;
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {
        //1-1=0     2-1=1    3-1=2    4-1=3
        //select * from student limit 10 offset (0*10)
        //select * from student limit 10 offset (1*10)
        //select * from student limit 10 offset (2*10)
        //select * from student limit 10 offset (3*10)
        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> studentPage = studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
        return studentPage;
    }

    //3. FACULTY DEKANAT
    @GetMapping("forFaculty/{facultyId}")
    public Page<Student> getStudentListForFaculty(@PathVariable Integer facultyId, @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroup_facultyId = studentRepository.findAllByGroup_FacultyId(facultyId, pageable);
        return allByGroup_facultyId;
    }

    //4. GROUP OWNER

    @GetMapping("forGroup/{groupId}")
    public Page<Student> getStudentListForGroup(@PathVariable Integer groupId, @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        Page<Student> allByGroupId = studentRepository.findAllByGroupId(groupId, pageable);
        return allByGroupId;
    }

    @PostMapping("addStudent")
    public String addStuydent(@RequestBody StudentDto studentDto) {
        Optional<Address> adressId = addressRepository.findById(studentDto.getAddressId());
        Optional<Group> groupId = groupRepository.findById(studentDto.getGroupId());
        List<Subject> subjects = null;
        List<Integer> subjectId = studentDto.getSubjectId();
        for (Integer id : subjectId) {
            Optional<Subject> byId = subjectRepository.findById(id);
            if (byId.isPresent()) {
                Subject subject = byId.get();
                subjects.add(subject);
            }
        }

        if (adressId.isPresent() && groupId.isPresent() && !subjects.isEmpty()) {
            Address address = adressId.get();
            Group group = groupId.get();
            Student student = new Student(null, studentDto.getFirstName(), studentDto.getLastName(), address, group, subjects);
            studentRepository.save(student);
            return "ADDED";
        }
        return "ERROR";
    }

    @PutMapping("/edit/{id}")
    public String editStudent(@PathVariable Integer id, @RequestBody StudentDto studentDto) {
        Optional<Student> studentById = studentRepository.findById(id);
        Optional<Address> addressById = addressRepository.findById(studentDto.getAddressId());
        Optional<Group> groupNyId = groupRepository.findById(studentDto.getGroupId());
        List<Subject> subjects = null;
        List<Integer> subjectId = studentDto.getSubjectId();
        for (Integer subId : subjectId) {
            Optional<Subject> byId = subjectRepository.findById(subId);
            if (byId.isPresent()) {
                Subject subject = byId.get();
                subjects.add(subject);
            }
        }

        if (studentById.isPresent() && addressById.isPresent() && groupNyId.isPresent() && !subjects.isEmpty()) {
            Student student = studentById.get();
            Address address = addressById.get();
            Group group = groupNyId.get();
            student.setFirstName(studentDto.getFirstName());
            student.setLastName(student.getLastName());
            student.setAddress(address);
            student.setGroup(group);
            student.setSubjects(subjects);
            studentRepository.save(student);
            return "EDITED";
        }
        return "this id is not found";
    }


    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id) {
        Optional<Student> byId = studentRepository.findById(id);
        if (byId.isPresent()){
            Student student = byId.get();
            studentRepository.deleteById(id);
            addressRepository.deleteById(student.getAddress().getId());
            return "DELETED";
        }else {
            return "This id is not found!";
        }

    }


}
