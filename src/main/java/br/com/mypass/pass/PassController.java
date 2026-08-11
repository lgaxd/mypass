package br.com.mypass.pass;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/pass")
public class PassController {

    private final PassRepository passRepository;

    public PassController(PassRepository passRepository) {
        this.passRepository = passRepository;
    }

    @GetMapping
    public List<Pass> getAllPasswords() {
        return passRepository.findAll();
    }
}