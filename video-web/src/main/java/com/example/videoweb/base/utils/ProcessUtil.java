package com.example.videoweb.base.utils;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.List;

/**
 * @Author: chailei
 * @Date: 2024/7/31 12:17
 */
@Slf4j
public class ProcessUtil {

    public static void main(String[] args) {
        String path = "/Users/anubis/Downloads/back/video/2024/07/31";
        String videoOutputPath = path + File.separator + "5" + ".mp4";
        String gifOutputPath = path + File.separator + "5" + ".gif";

        double totalSeconds = Double.parseDouble(ProcessUtil.executeCommandWithResult(
                Arrays.asList("ffprobe", "-v", "error", "-show_entries", "format=duration", "-of",
                        "default=noprint_wrappers=1:nokey=1", videoOutputPath)
        ));

        boolean gifSuccess = ProcessUtil.executeCommand(
                Arrays.asList("ffmpeg", "-i", videoOutputPath, "-t", "5", "-pix_fmt", "rgb24", gifOutputPath));
        if (!gifSuccess) {
            log.error("Failed to generate GIF thumbnail.");
        }

        System.out.println(totalSeconds);
    }

    public static boolean executeCommand(List<String> command) {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            // 创建两个线程来分别读取标准输出和错误输出
            Thread stdoutThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.info("ffmpeg output: {}", line);
                    }
                } catch (IOException e) {
                    log.error("Error reading ffmpeg output: {}", e.getMessage());
                }
            });

            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.error("ffmpeg error: {}", line);
                    }
                } catch (IOException e) {
                    log.error("Error reading ffmpeg error output: {}", e.getMessage());
                }
            });

            stdoutThread.start();
            stderrThread.start();

            // 等待ffmpeg进程结束
            int exitCode = process.waitFor();

            // 确保所有的流读取线程都已完成
            stdoutThread.join();
            stderrThread.join();

            if (exitCode != 0) {
                readAndLogErrorStream(process);
                return false;
            }
            return true;
        } catch (IOException | InterruptedException e) {
            log.error("Error executing command: {}", e.getMessage());
            return false;
        }
    }

    private static ThreadLocal<StringBuilder> threadLocalResult = ThreadLocal.withInitial(StringBuilder::new);

    public static String executeCommandWithResult(List<String> command) {
        StringBuilder result = threadLocalResult.get();
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        try {
            Process process = processBuilder.start();
            // 创建线程读取标准输出
            Thread stdoutThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    StringBuilder output = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        output.append(line).append("\n");
                    }
                    result.append(output.toString().trim());
                } catch (IOException | NumberFormatException e) {
                    log.error("Error reading from process output: {}", e.getMessage());
                }
            });

            // 创建线程读取错误输出
            Thread stderrThread = new Thread(() -> {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        log.error("Error from process: {}", line);
                    }
                    result.append("2"); //错误则返回默认值 2秒
                } catch (IOException e) {
                    log.error("Error reading from process error stream: {}", e.getMessage());
                }
            });

            stdoutThread.start();
            stderrThread.start();

            int exitCode = process.waitFor();

            // 确保读取输出的线程也已经完成
            stdoutThread.join();
            stderrThread.join();

            if (exitCode != 0) {
                readAndLogErrorStream(process);
                throw new RuntimeException("Command failed with exit code: " + exitCode);
            }
            return result.toString();
        } catch (IOException | InterruptedException | NumberFormatException e) {
            log.error("Error executing command or parsing result: {}", e.getMessage());
            throw new RuntimeException(e);
        } finally {
            threadLocalResult.remove();
        }
    }



    private static void readAndLogErrorStream(Process process) throws IOException {
        BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
        StringBuilder errorOutput = new StringBuilder();
        String line;
        while ((line = errorReader.readLine()) != null) {
            errorOutput.append(line).append("\n");
        }
        errorReader.close();
        log.error("Error output: {}", errorOutput.toString());
    }
}