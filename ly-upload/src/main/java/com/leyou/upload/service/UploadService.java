package com.leyou.upload.service;

import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;


/**
 * @author: xiepanpan
 * @Date: 2019/7/28
 * @Description: 图片上传service
 */
@Service
@Slf4j
public class UploadService {

    /**
     * 允许的图片类型
     */
    private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg","image/png","image/bmp");

    /**
     * 图片上传功能
     * @param file
     * @return
     */
    public String uploadImage(MultipartFile file) {
        try {
            //校验文件
            //文件类型
            String contentType = file.getContentType();
            if (!ALLOW_TYPES.contains(contentType)) {
                throw new LeYouException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image==null) {
                throw new LeYouException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //准备目标路径
            File dest = new File("D:\\project\\leyou\\upload",file.getOriginalFilename());
            //保存文件到本地
            file.transferTo(dest);
            return "http://image.leyou.com/"+file.getOriginalFilename();
        } catch (IOException e) {
            log.info("文件上传失败",e);
            throw  new LeYouException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
