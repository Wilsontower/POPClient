import java.io.*;
import java.net.Socket;
import java.util.Scanner;

public class POP {
    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);
        Command commands = new CommandImpl();

        BufferedReader in;
        BufferedWriter out;

        //连接
        while (true) {
            System.out.printf("> ");
            String[] conn = scanner.nextLine().split(" ");
            if (conn.length != 3) {
                System.out.println("USAGE: MyPOP [POP Server] [Port]");
                System.out.println("eg: MyPOP pop3.163.com 110");
                continue;
            }

            String popServer = conn[1];
            int port = Integer.valueOf(conn[2]);

            Socket socket = null;
            try {
                socket = new Socket(popServer, port);
            } catch (Exception e) {
                System.out.println("CANNOT CREATE THE SOCKET");
                System.out.println("USAGE: MyPOP [POP Server] [Port]");
                System.out.println("eg: MyPOP pop3.163.com 110");
                continue;
            }

            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            String msg = commands.getReturn(in);
            System.out.println(msg);
            String result = commands.getResult(msg);
            if (!"+OK".equals(result)) {
                System.out.println("CANNOT CONNECT TO MAIL SERVER");
                System.out.println("USAGE: MyPOP [POP Server] [Port]");
                System.out.println("eg: MyPOP pop3.163.com 110");
                continue;
            }

            break;
        }

       //验证
        while (true) {
            System.out.printf("> ");
            String[] userParam = scanner.nextLine().split(" ");

            if (userParam.length != 2 || !userParam[0].equals("user")) {
                System.out.println("Please access the authorization by username and password first.");
                continue;
            }
            System.out.println(commands.user(userParam[1], in, out));

            System.out.printf("> ");
            String[] passParam = scanner.nextLine().split(" ");
            if (passParam.length != 2 || !passParam[0].equals("pass")) {
                System.out.println("Please access the authorization by username and password first.");
                continue;
            }
            System.out.println(commands.pass(passParam[1], in, out));

            break;
        }

        //操作处理
        while (true) {
            System.out.printf("> ");
            String[] cmd = scanner.nextLine().split(" ");

            switch (cmd[0]) {
                case "stat":
                    System.out.println(commands.stat(in, out));
                    continue;
                case "list":
                    if (cmd.length == 1) {
                        System.out.println(commands.list(in, out));
                    } else {
                        System.out.println(commands.list(Integer.valueOf(cmd[1]), in, out));
                    }
                    continue;
                case "retr":
                    if (cmd.length == 2) {
                        commands.retr(Integer.valueOf(cmd[1]), in, out);
                    } else {
                        System.out.println("USAGE: retr [Mail Num]");
                    }
                    continue;
                case "dele":
                    if (cmd.length == 2) {
                        commands.dele(Integer.valueOf(cmd[1]), in, out);
                    } else {
                        System.out.println("USAGE: dele [Mail Num]");
                    }
                    continue;
                case "quit":
                    commands.quit(in, out);
                    return;
            }

            System.out.println("Please use the cmd like stat, list, retr, dele or quit.");
        }

    }
}
