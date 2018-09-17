package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author: HuYi.Zhang
 * @create: 2018-07-21 10:03
 **/
@Service
public class UploadService {

    @Autowired
    private FastFileStorageClient storageClient;

    private static final List<String> ALLOW_CONTENT_TYPE = Arrays.asList("image/png", "image/jpeg");

    public String uploadImage(MultipartFile file) {
        try {
            // 图片的校验
            // 1、校验的是文件的类型
            String contentType = file.getContentType();
            if(!ALLOW_CONTENT_TYPE.contains(contentType)){
                return null;
            }

            // 2、校验文件内容
            BufferedImage bufferedImage = ImageIO.read(file.getInputStream());
            if(bufferedImage == null){
                return null;
            }

            // 文件上传目的地
            // File dest = new File("D:\\heima32\\nginx-1.12.2\\html\\" + file.getOriginalFilename());
            // 上传
            // file.transferTo(dest);

            // 保存到FastDFS
            String extName = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
            StorePath path = this.storageClient.uploadFile(file.getInputStream(), file.getSize(), extName, null);

            return "http://image.leyou.com/" + path.getFullPath();
        } catch (Exception e){
            throw new RuntimeException(e);
        }
    }
}
