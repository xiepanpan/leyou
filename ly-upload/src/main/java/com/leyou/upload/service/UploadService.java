package com.leyou.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.leyou.common.enums.ExceptionEnum;
import com.leyou.common.exception.LeYouException;
import com.leyou.upload.config.UploadProperties;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
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
@EnableConfigurationProperties(UploadProperties.class)
public class UploadService {

    @Autowired
    private FastFileStorageClient fastFileStorageClient;
    @Autowired
    private UploadProperties uploadProperties;

    /**
     * 允许的图片类型
     */
//    private static final List<String> ALLOW_TYPES = Arrays.asList("image/jpeg","image/png","image/bmp");

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
            if (!uploadProperties.getAllowTypes().contains(contentType)) {
                throw new LeYouException(ExceptionEnum.INVALID_FILE_TYPE);
            }

            //校验文件内容
            BufferedImage image = ImageIO.read(file.getInputStream());
            if (image==null) {
                throw new LeYouException(ExceptionEnum.INVALID_FILE_TYPE);
            }
            //上传到FastDFS
            String extension = StringUtils.substringAfterLast(file.getOriginalFilename(),".");
            StorePath storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), extension, null);

            //保存文件到本地
            return uploadProperties.getBaseUrl()+storePath.getFullPath();
        } catch (IOException e) {
            log.info("[文件上传]文件上传失败",e);
            throw  new LeYouException(ExceptionEnum.UPLOAD_FILE_ERROR);
        }
    }
}
