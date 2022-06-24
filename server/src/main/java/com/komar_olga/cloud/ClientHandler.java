package com.komar_olga.cloud;

import com.komar_olga.cloud.model.FileList;
import com.komar_olga.cloud.model.FileMessage;
import com.komar_olga.cloud.model.FileRename;
import com.komar_olga.cloud.model.FileRequest;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;

import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ClientHandler extends ChannelInboundHandlerAdapter {
    private static final List<Channel> channels = new ArrayList<>();
    private String clientName, login, pass;
    private static int newClientIndex = 1;
    private static ObjectEncoderOutputStream out;
    private String clientFolder = "server_storage/";


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if (msg instanceof FileRequest) {
            FileRequest fr = (FileRequest) msg;

            //авторизация
            if (fr.getActionPoint().equals("/auth")) {
                String[] parts = fr.getFileName().split("\\s");
                login = parts[0];
                pass = parts[1];
                int index = Integer.parseInt(parts[2]);
                JDBCConnect jdbcConnect = new JDBCConnect(login, pass, index);
                String actionPoint = jdbcConnect.getActionPoint();
                int id = jdbcConnect.getId();
                String nick = jdbcConnect.getNick();
                ctx.writeAndFlush(new FileRequest(actionPoint, "auth"));
                clientFolder = "server_storage_id" + id + "/";
                Path newFolderPath = Paths.get(clientFolder);
                try {
                    Files.createDirectories(newFolderPath);
                    ctx.writeAndFlush(new FileList(clientFolder));
                } catch (FileAlreadyExistsException e) {
                    this.clientFolder = "server_storage_id" + id + "/";
                    System.out.println("Каталог с таким именем уже существует");
                } catch (IOException e) {
                    System.out.println("что-то пошло не так при чтении");
                }
            }

            if (fr.getFileName() != null) {
                if (Files.exists(Paths.get(clientFolder + fr.getFileName()))) {
                    // удаление
                    if (fr.getActionPoint().equals("delete")) {
                        Path path = Paths.get(clientFolder + fr.getFileName());
                        System.out.println(clientFolder + fr.getFileName() + " del");
                        Files.delete(path);
                        ctx.writeAndFlush(new FileList(clientFolder));
                    }
                    //скачивание
                    if (fr.getActionPoint().equals("download")) {
                        FileMessage fm = new FileMessage(Paths.get(clientFolder + fr.getFileName()), "download");
                        ctx.writeAndFlush(fm);
                    }
                }
            }
        }


        if (msg instanceof FileMessage) {
            FileMessage fm = (FileMessage) msg;
            if (fm.getActionPoint().equals("upload")) {
                Files.write(Paths.get(clientFolder + fm.getFileName()), fm.getData(), StandardOpenOption.CREATE);
                System.out.println(clientFolder + fm.getFileName());
                ctx.writeAndFlush(new FileList(clientFolder));
            }

        }

        if (msg instanceof FileRename) {
            FileRename fr = (FileRename) msg;

            System.out.print(clientFolder + fr.getSourcePath()+"->");
            Path sourcePath = Paths.get(clientFolder + fr.getSourcePath());
            System.out.println(clientFolder + fr.getDestinationPath());
            Path destinationPath = Paths.get(clientFolder + fr.getDestinationPath());

            Files.move(sourcePath, destinationPath);
            ctx.writeAndFlush(new FileList(clientFolder));
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Клиент подключился! " + ctx);
        channels.add(ctx.channel());

    }


}
