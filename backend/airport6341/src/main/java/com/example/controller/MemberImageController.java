package com.example.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping(value = "/memberimage")
public class MemberImageController {

    // @Autowired MemberImgRepository miRepository;

    // @GetMapping(value = "/insert.do")
    // public String insertGET(){

    //     return "image_insert";
    // }


    // @PostMapping(value = "/insert.do")
    // public String insertPOST(
    //         @RequestParam(name = "file") MultipartFile file,
    //         @ModelAttribute MemberImg image)throws IOException{
    //     System.out.println(image.toString());

    //     image.setImagename(file.getOriginalFilename());
    //     image.setImagesize(file.getSize());
    //     image.setImagedata(file.getBytes());
    //     image.setImagetype(file.getContentType());
    //     miRepository.save(image);

    //     return "redirect:/";
    // }
}
