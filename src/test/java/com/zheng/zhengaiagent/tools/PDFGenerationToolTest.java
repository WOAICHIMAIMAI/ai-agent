package com.zheng.zhengaiagent.tools;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PDFGenerationToolTest {

    @Test
    void generatePDF() {
        PDFGenerationTool pdfGenerationTool = new PDFGenerationTool();
        String fileName = "ai智能体.pdf";
        String content = "pdf生成工具测试";
        String result = pdfGenerationTool.generatePDF(fileName, content);
        assertNotNull(result);
    }
}