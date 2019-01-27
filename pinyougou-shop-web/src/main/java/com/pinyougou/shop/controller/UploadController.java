package com.pinyougou.shop.controller;

import entity.ResultInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import util.FastDFSClient;

@RestController
public class UploadController {
    @Value("${FILE_SERVER_URL}")
    private String file_server_url;

    @RequestMapping("/upload")
    public ResultInfo upload(MultipartFile file) {
        try {
            //获取文件名称
            String filename = file.getOriginalFilename();
            //截取扩展名
            String extName = filename.substring(filename.lastIndexOf(".") + 1);
            //建立fastDFS服务器连接
            FastDFSClient fastDFSClient = new FastDFSClient("classpath:config/fdfs_client.conf");
            //上传文件并返回文件ID
            String fileId = fastDFSClient.uploadFile(file.getBytes(), extName);
            //拼接url与文件ID
            String url = file_server_url + fileId;
            return new ResultInfo(true, url);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResultInfo(false, "上传失败");
        }
    }
}
