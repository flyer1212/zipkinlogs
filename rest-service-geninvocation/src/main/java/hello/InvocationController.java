package hello;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class InvocationController {

    @GetMapping("/startCopyFileToHdfs")
    public void startCopyFileToCsv() {
        System.out.println("===========start copy file to hdfs ========");
        HDFSApiDemo.getResourceData();
    }

    @GetMapping("/stopCopyFileToHdfs")
    public String stopCollectResourceData() {
        System.out.println("===========stop copy file to hdfs ========");
        return HDFSApiDemo.stopCopyFileToHDFS();
    }

    @GetMapping("/hello")
    public String hello(){
        System.out.println("============hello ===========");
        return "[hello World!]";
    }
}
