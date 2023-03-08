import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Date;


public class SystemInfo {
    public static void main(String[] args) {
        try {
            // Creates a file to save data retrieved
            File file = new File("system_info.txt");
            FileWriter writer = new FileWriter(file);

            // Gets current date and time
            Date date = new Date();

            // Writes current date and time
            writer.write("System Information - " + date.toString() + "\n\n");

            // Gets the computer name and IP address
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            writer.write("Computer Name: " + hostname + "\n");
            writer.write("IP Address: " + ip.getHostAddress() + "\n\n");

            // Gets the MAC address of the first network interface
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            writer.write("MAC Address: " + sb.toString() + "\n\n");

            // Gets the system's CPU usage
            double cpuUsage = getCPUUsage();
            writer.write("CPU Usage: " + new DecimalFormat("#.##").format(cpuUsage) + " %\n\n");

            // Gets the list of running services
            String runningServices = runApplication();
            writer.write("Services Running: " + runningServices + "\n");


            // Close the FileWriter
            writer.close();

            System.out.println("System information saved to system_info.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Gets the current CPU usage
    private static double getCPUUsage() {
        try {
            // Executes the "wmic cpu get loadpercentage" command
            Process process = Runtime.getRuntime().exec("wmic cpu get loadpercentage");
            process.getOutputStream().close();

            String output = new String(process.getInputStream().readAllBytes());
            String[] lines = output.split("\n");

            // Extracts the CPU usage
            String value = lines[1].trim();
            double cpuUsage = Double.parseDouble(value);

            return cpuUsage;
        } catch (IOException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private static String runApplication() {
        StringBuilder sb = new StringBuilder();
        try {
            Process process = Runtime.getRuntime().exec("tasklist /v /fo csv");
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            // Skip header
            String line = bufferedReader.readLine();

            // Reads all the lines and extract the service name
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                String serviceName = parts[0].replaceAll("\"", "");
                sb.append(serviceName).append("\n");
            }

            // Closes the streams
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
