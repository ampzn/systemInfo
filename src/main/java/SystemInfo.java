import java.io.*;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.text.DecimalFormat;
import java.util.Date;


public class SystemInfo {
    public static void main(String[] args) {
        try {
            // Create a new File object for the system_info.txt file
            File file = new File("system_info.txt");

            // Create a new FileWriter object to write to the file
            FileWriter writer = new FileWriter(file);

            // Get the current date and time
            Date date = new Date();

            // Write the current date and time to the file
            writer.write("System Information - " + date.toString() + "\n\n");

            // Get the computer name and IP address
            InetAddress ip = InetAddress.getLocalHost();
            String hostname = ip.getHostName();
            writer.write("Computer Name: " + hostname + "\n");
            writer.write("IP Address: " + ip.getHostAddress() + "\n\n");

            // Get the MAC address of the first network interface
            NetworkInterface network = NetworkInterface.getByInetAddress(ip);
            byte[] mac = network.getHardwareAddress();
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mac.length; i++) {
                sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
            }
            writer.write("MAC Address: " + sb.toString() + "\n\n");

            // Get the system's CPU usage
            double cpuUsage = getCPUUsage();
            writer.write("CPU Usage: " + new DecimalFormat("#.##").format(cpuUsage) + " %\n\n");

            // Get the list of running services
            String runningServices = runApplication();
            writer.write("Services Running: " + runningServices + "\n");


            // Close the FileWriter
            writer.close();

            System.out.println("System information saved to system_info.txt");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Method to get the current CPU usage
    private static double getCPUUsage() {
        try {
            // Execute the "wmic cpu get loadpercentage" command and parse the output
            Process process = Runtime.getRuntime().exec("wmic cpu get loadpercentage");
            process.getOutputStream().close();

            String output = new String(process.getInputStream().readAllBytes());
            String[] lines = output.split("\n");

            // Extract the CPU usage value from the output
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

            // Skip the first line (header)
            String line = bufferedReader.readLine();

            // Read the rest of the lines and extract the service name
            while ((line = bufferedReader.readLine()) != null) {
                String[] parts = line.split(",");
                String serviceName = parts[0].replaceAll("\"", "");
                sb.append(serviceName).append("\n");
            }

            // Close the streams
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }


}
