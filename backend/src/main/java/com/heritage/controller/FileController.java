package com.heritage.controller;

import cn.hutool.core.util.StrUtil;
import com.heritage.common.Result;
import com.heritage.common.ResultCode;
import com.heritage.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

/**
 * 文件上传接口（需登录）：支持图片、视频上传到本地目录，返回可访问的 URL
 *
 * <p>安全策略：
 * 1. 类型白名单按扩展名校验，扩展名仅允许字母数字（防路径拼接）；
 * 2. 存储文件名使用 UUID 重新生成，不使用用户原始文件名；
 * 3. 大小限制：图片 10MB、视频 500MB（框架层 multipart 上限另在 yml 配置）。</p>
 */
@Slf4j
@RestController
@RequestMapping("/api/file")
public class FileController {

    /** 图片扩展名白名单 */
    private static final List<String> IMAGE_EXT = Arrays.asList("jpg", "jpeg", "png", "gif", "webp", "bmp");

    /** 视频扩展名白名单 */
    private static final List<String> VIDEO_EXT = Arrays.asList("mp4", "webm", "mov", "avi", "mkv");

    /** 大小限制（字节）：图片 10MB，视频 500MB */
    private static final Map<String, Long> MAX_SIZE = Map.of(
            "image", 10L * 1024 * 1024,
            "video", 500L * 1024 * 1024);

    @Value("${heritage.upload.path}")
    private String uploadPath;

    /**
     * 上传文件（需登录）
     *
     * @param file 表单文件字段
     * @param type 文件类型：image-图片 / video-视频
     * @return 文件访问 URL（如 http://localhost:8080/files/image/20261004/xxx.png）
     */
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file,
                                 @RequestParam(defaultValue = "image") String type) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "上传文件不能为空");
        }
        if (!"image".equals(type) && !"video".equals(type)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "type 仅支持 image 或 video");
        }

        // 扩展名白名单校验（原始文件名仅用于取扩展名，不参与存储命名）
        String ext = extensionOf(file.getOriginalFilename());
        List<String> whitelist = "image".equals(type) ? IMAGE_EXT : VIDEO_EXT;
        if (!whitelist.contains(ext)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "不支持的文件类型 ." + ext + "，允许：" + String.join("/", whitelist));
        }
        if (file.getSize() > MAX_SIZE.get(type)) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(),
                    "image".equals(type) ? "图片最大10MB" : "视频最大500MB");
        }

        // 存储：{上传目录}/{type}/{日期}/{UUID}.{ext}，目录不存在自动创建
        String dateDir = new SimpleDateFormat("yyyyMMdd").format(new Date());
        String relativePath = type + "/" + dateDir + "/" + UUID.randomUUID().toString().replace("-", "") + "." + ext;
        Path target = Paths.get(uploadPath).toAbsolutePath().normalize().resolve(relativePath);
        try {
            Files.createDirectories(target.getParent());
            file.transferTo(target.toFile());
        } catch (Exception e) {
            log.error("文件保存失败：{}", relativePath, e);
            throw new BusinessException("文件保存失败，请稍后重试");
        }

        // 拼接访问 URL：http://host:port/files/{type}/{日期}/{UUID}.{ext}
        String url = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString()
                + "/files/" + relativePath;
        log.info("文件上传成功：{}", relativePath);
        return Result.ok(url);
    }

    /** 提取并校验扩展名：仅允许1-10位字母数字，防止特殊字符进入存储路径 */
    private String extensionOf(String originalFilename) {
        if (StrUtil.isBlank(originalFilename) || originalFilename.lastIndexOf('.') < 0) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "文件缺少扩展名");
        }
        String ext = originalFilename.substring(originalFilename.lastIndexOf('.') + 1)
                .toLowerCase(Locale.ROOT);
        if (!ext.matches("^[a-z0-9]{1,10}$")) {
            throw new BusinessException(ResultCode.PARAM_ERROR.getCode(), "非法的文件扩展名");
        }
        return ext;
    }
}
