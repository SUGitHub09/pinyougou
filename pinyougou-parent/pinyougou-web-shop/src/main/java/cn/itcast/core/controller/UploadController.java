package cn.itcast.core.controller;

import cn.itcast.common.utils.FastDFSClient;
import entity.Result;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${FILE_SERVER_URL}")
    private String FILE_SERVER_URL;
    @RequestMapping("/uploadFile")
    public Result uploadFile(MultipartFile file)  {
        try {
            System.out.println(file.getOriginalFilename());
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:fastDFS/fdfs_client.conf");
            String ext= FilenameUtils.getExtension(file.getOriginalFilename());
            String s = fastDFSClient.uploadFile(file.getBytes(), ext, null);

            return new Result(true,FILE_SERVER_URL+s);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"失败");
        }
    }
}
