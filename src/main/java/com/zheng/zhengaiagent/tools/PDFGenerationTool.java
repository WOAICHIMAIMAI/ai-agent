package com.zheng.zhengaiagent.tools;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.zheng.zhengaiagent.constant.FileConstant;
import com.zheng.zhengaiagent.properties.AliOssProperties;
import com.zheng.zhengaiagent.util.AliOSSUtil;
import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

/**
 * PDF 生成工具 通过OSS存储
 */
@Slf4j
@Component
public class PDFGenerationTool {

    private AliOSSUtil aliOSSUtil;

    public PDFGenerationTool(AliOssProperties aliOssProperties){
        aliOSSUtil = new AliOSSUtil(aliOssProperties.getEndpoint(), aliOssProperties.getAccessKeyId(), aliOssProperties.getAccessKeySecret(), aliOssProperties.getBucketName());
    }

    @Tool(description = "Generate a PDF file with given content", returnDirect = true)
    public String generatePDF(
            @ToolParam(description = "Name of the file to save the generated PDF") String fileName,
            @ToolParam(description = "Content to be included in the PDF") String content) {
        String fileDir = FileConstant.FILE_SAVE_DIR + "/pdf";
        String filePath = fileDir + "/" + fileName;
        String urlPath;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            // 1. 创建PDF到内存流
            try (PdfWriter writer = new PdfWriter(baos);
                 PdfDocument pdf = new PdfDocument(writer);
                 Document document = new Document(pdf)) {

                // 设置中文字体
                PdfFont font = PdfFontFactory.createFont("STSongStd-Light", "UniGB-UCS2-H");
                document.setFont(font);

                // 添加内容
                Paragraph paragraph = new Paragraph(content);
                document.add(paragraph);
            }

            // 2. 从内存流获取字节数组并上传到OSS
            byte[] pdfBytes = baos.toByteArray();
            String objectName = UUID.randomUUID() + ".pdf";
            urlPath = aliOSSUtil.uploadFile(objectName, pdfBytes);

            log.info("PDF generated and uploaded to OSS successfully. URL: {}", urlPath);
            return "PDF generated successfully to: " + urlPath;

        } catch (IOException e) {
            log.error("Error generating PDF", e);
            return "Error generating PDF: " + e.getMessage();
        }
    }
}
