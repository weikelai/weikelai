package com.myblog.firstjavaproject.javawork;

import java.io.*;
import java.util.*;

/**
 * @Author: 赖国燕
 * @CreateTime: 2025-12-06
 * @Description: 用哈夫曼编码对一纯英文文本压缩
 * @Version: 1.0
 * 使用说明:
 *   编译: javac HuffmanCompress.java
 *   压缩: java HuffmanCompress compress input.txt output.huff
 *   解压: java HuffmanCompress decompress input.huff output.txt
 *
 * 说明：
 * - 程序按字节统计频率（适合纯英文 ASCII 文本）。
 * - 在压缩文件头写入：总原始字节数 (long)，唯一符号数 (int)，每个符号 (1 byte) + 频率 (long)。
 * - 这样解压时可以精确恢复哈夫曼树，无需额外元数据。
 *
 * 重要：为保持实现简洁，频率使用 long，文件头结构简单且稳健。
 */

public class HuffmanCompress {

    // Huffman 树节点
    private static class Node implements Comparable<Node>, Serializable {
        final long freq;
        final Byte b; // null 表示非叶子节点
        Node left, right;

        Node(long freq, Byte b) {
            this.freq = freq;
            this.b = b;
        }

        Node(long freq, Node left, Node right) {
            this.freq = freq;
            this.b = null;
            this.left = left;
            this.right = right;
        }

        boolean isLeaf() {
            return b != null;
        }

        @Override
        public int compareTo(Node o) {
            // 频率升序；如频率相同，保持稳定性再比较字节（避免 null 比较问题）
            int cmp = Long.compare(this.freq, o.freq);
            if (cmp != 0) return cmp;
            if (this.b == null && o.b == null) return 0;
            if (this.b == null) return 1;
            if (o.b == null) return -1;
            return Byte.compare(this.b, o.b);
        }
    }

    // 按位写入流（简单实现）
    private static class BitOutputStream implements Closeable {
        private final OutputStream out;
        private int currentByte = 0;
        private int numBitsFilled = 0;

        BitOutputStream(OutputStream out) {
            this.out = out;
        }

        // 写入一个比特 (0 or 1)
        void writeBit(int bit) throws IOException {
            if (bit != 0 && bit != 1) throw new IllegalArgumentException("bit must be 0 or 1");
            currentByte = (currentByte << 1) | bit;
            numBitsFilled++;
            if (numBitsFilled == 8) {
                out.write(currentByte);
                numBitsFilled = 0;
                currentByte = 0;
            }
        }

        // 写入字符串形式的比特序列 "0101..."
        void writeBits(String bits) throws IOException {
            for (int i = 0; i < bits.length(); i++) {
                char c = bits.charAt(i);
                if (c == '0') writeBit(0);
                else if (c == '1') writeBit(1);
                else throw new IllegalArgumentException("bits string must contain only '0' or '1'");
            }
        }

        // 将剩余比特补齐到字节边界（高位补0），并返回补齐的位数（0-7）
        int flushWithPadding() throws IOException {
            int padding = 0;
            if (numBitsFilled > 0) {
                // 补齐到 8 位（在当前实现中我们已经是左移写，需将末尾补位）
                currentByte = currentByte << (8 - numBitsFilled);
                out.write(currentByte);
                padding = 8 - numBitsFilled;
                numBitsFilled = 0;
                currentByte = 0;
            }
            out.flush();
            return padding;
        }

        @Override
        public void close() throws IOException {
            flushWithPadding();
            out.close();
        }
    }

    // 按位读取流（简单实现）
    private static class BitInputStream implements Closeable {
        private final InputStream in;
        private int currentByte = 0;
        private int numBitsRemaining = 0;

        BitInputStream(InputStream in) {
            this.in = in;
        }

        // 读取一位，返回 -1 表示已到流末尾
        int readBit() throws IOException {
            if (numBitsRemaining == 0) {
                currentByte = in.read();
                if (currentByte == -1) return -1;
                numBitsRemaining = 8;
            }
            numBitsRemaining--;
            return (currentByte >> numBitsRemaining) & 1;
        }

        @Override
        public void close() throws IOException {
            in.close();
        }
    }

    // 生成哈夫曼编码表：byte -> bit string (e.g. "0101")
    private static void buildCodeTable(Node root, String prefix, Map<Byte, String> codeMap) {
        if (root == null) return;
        if (root.isLeaf()) {
            codeMap.put(root.b, prefix.length() > 0 ? prefix : "0"); // 只有一种符号时，编码为 "0"
            return;
        }
        buildCodeTable(root.left, prefix + '0', codeMap);
        buildCodeTable(root.right, prefix + '1', codeMap);
    }

    // 构建哈夫曼树
    private static Node buildHuffmanTree(Map<Byte, Long> freqMap) {
        PriorityQueue<Node> pq = new PriorityQueue<>();
        for (Map.Entry<Byte, Long> e : freqMap.entrySet()) {
            pq.add(new Node(e.getValue(), e.getKey()));
        }
        // 边界情况：只有一种符号 => 创建虚拟另一个节点使树有两叶
        if (pq.size() == 1) {
            Node only = pq.poll();
            Node fake = new Node(0L, (byte) (~only.b)); // 一个不同的字节（频率0）
            pq.add(only);
            pq.add(fake);
        }
        while (pq.size() > 1) {
            Node a = pq.poll();
            Node b = pq.poll();
            Node parent = new Node(a.freq + b.freq, a, b);
            pq.add(parent);
        }
        return pq.poll();
    }

    // 将频率表写入压缩文件头（用于解压时重建哈夫曼树）
    // Header layout:
    // [magic 4 bytes] 'H','U','F','F'
    // [originalBytes long 8 bytes] 原始总字节数
    // [uniqueCount int 4 bytes] 唯一字节个数
    // repeated uniqueCount times:
    //   [byte 1] 符号 (0-255)
    //   [freq long 8 bytes] 该符号的频率
    // [padding byte 1] 压缩数据末尾补齐的位数 (0-7)
    // 后面接实际按位写入的压缩比特流（从高位到低位写入）
    private static final byte[] MAGIC = new byte[]{'H','U','F','F'};

    // 压缩文件
    public static void compressFile(String inputPath, String outputPath) throws IOException {
        // 读取整个文件并统计频率
        File inFile = new File(inputPath);
        if (!inFile.exists() || !inFile.isFile()) throw new FileNotFoundException("Input file not found");
        long originalBytes = inFile.length();

        Map<Byte, Long> freqMap = new HashMap<>();
        try (InputStream in = new BufferedInputStream(new FileInputStream(inFile))) {
            int x;
            while ((x = in.read()) != -1) {
                byte b = (byte) x;
                freqMap.put(b, freqMap.getOrDefault(b, 0L) + 1L);
            }
        }

        if (freqMap.isEmpty()) {
            throw new IllegalArgumentException("Input file is empty");
        }

        Node root = buildHuffmanTree(freqMap);
        Map<Byte, String> codeMap = new HashMap<>();
        buildCodeTable(root, "", codeMap);

        // 写文件头和按位数据
        try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(outputPath))) {

            fout.write(MAGIC);

            writeLong(fout, originalBytes);

            writeInt(fout, freqMap.size());

            for (Map.Entry<Byte, Long> e : freqMap.entrySet()) {
                fout.write(e.getKey()); // 写入符号字节（可能为负值，但写入其二进制表示）
                writeLong(fout, e.getValue());
            }

            // 接下来我们需要把编码后的比特流写入，但是 padding 值（尾部补齐位数）需要写在 header，
            // 我们先把 header 写完，然后使用 BitOutputStream 向 fout 写入比特数据。
            // 为了在 header 中写入 padding，我们需要在写入比特数据后再回写 padding。
            // 简单做法：先把 header 写好（暂时把 padding 写为0），记住 header 长度，然后在最后使用 RandomAccessFile 来修改 padding 字节。
            // 为实现简单，我们将把 padding 写入 header 的最后一个字节位置：在这里，我们先写一个占位 1 字节 0:
            fout.write(0); // padding 占位

            fout.flush(); // 确保 header 写入磁盘

            // 现在我们要写比特流。使用 BitOutputStream 包装一个 FileOutputStream 的附带流，但注意我们已经写入 header 的部分数据到 fout，
            // 直接继续往 fout 写入比特数据是可以的。
            BitOutputStream bos = new BitOutputStream(fout); // 继续使用同一流
            // 重新读取输入文件，并按 codeMap 写入比特
            try (InputStream in = new BufferedInputStream(new FileInputStream(inFile))) {
                int xx;
                while ((xx = in.read()) != -1) {
                    byte b = (byte) xx;
                    String code = codeMap.get(b);
                    bos.writeBits(code);
                }
                int padding = bos.flushWithPadding(); // 返回补齐位数
            } finally {

            }
        }

        // ---------- 为了代码的清晰性与正确性，下面使用可靠的实现：先将编码比特写入一个临时文件，再计算 padding 并写入最终输出文件 ----------
        File tempBits = File.createTempFile("huff_bits_", ".bin");
        tempBits.deleteOnExit();

        // 写入比特到 tempBits
        try (BitOutputStream bos = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(tempBits)))) {
            try (InputStream in = new BufferedInputStream(new FileInputStream(inFile))) {
                int xx;
                while ((xx = in.read()) != -1) {
                    byte b = (byte) xx;
                    String code = codeMap.get(b);
                    bos.writeBits(code);
                }
            }
            int padding = bos.flushWithPadding(); // 补齐位数
            // 记录 padding value into a small file attribute? We'll pass it later by reading length.
        }

        // 现在将 header + padding + tempBits 内容写入最终输出文件
        try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            fout.write(MAGIC);
            writeLong(fout, originalBytes);
            writeInt(fout, freqMap.size());
            for (Map.Entry<Byte, Long> e : freqMap.entrySet()) {
                fout.write(e.getKey());
                writeLong(fout, e.getValue());
            }
            // 读取 tempBits 长度以及最后一个字节中的有效位数，来计算 padding
            long bitsFileLen = tempBits.length(); // bytes
            int padding;
            if (bitsFileLen == 0) {
                padding = 0;
            } else {
                // 读取 tempBits 最后一个字节，计算其中补齐的 0 位数
                try (RandomAccessFile raf = new RandomAccessFile(tempBits, "r")) {
                    raf.seek(bitsFileLen - 1);
                    int last = raf.read();
                }
            }
        }

        // To avoid messy incremental edits above, do a clear, robust implementation below from scratch:
        // 1) 写 bits 到 tempBits 并同时把 padding 写到 tempPad 文件
        // 2) 然后把 header + padding + tempBits 内容写入 outputPath


        File tempPad = File.createTempFile("huff_pad_", ".pad");
        tempPad.deleteOnExit();


        int paddingValue;
        try (BitOutputStream bos = new BitOutputStream(new BufferedOutputStream(new FileOutputStream(tempBits)))) {
            try (InputStream in = new BufferedInputStream(new FileInputStream(inFile))) {
                int xx;
                while ((xx = in.read()) != -1) {
                    byte b = (byte) xx;
                    String code = codeMap.get(b);
                    bos.writeBits(code);
                }
            }
            paddingValue = bos.flushWithPadding();
        }

        try (FileOutputStream pf = new FileOutputStream(tempPad)) {
            pf.write(paddingValue);
        }


        try (OutputStream fout = new BufferedOutputStream(new FileOutputStream(outputPath))) {
            fout.write(MAGIC);
            writeLong(fout, originalBytes);
            writeInt(fout, freqMap.size());
            for (Map.Entry<Byte, Long> e : freqMap.entrySet()) {
                fout.write(e.getKey());
                writeLong(fout, e.getValue());
            }

            fout.write(paddingValue & 0xFF);

            try (InputStream bin = new BufferedInputStream(new FileInputStream(tempBits))) {
                byte[] buffer = new byte[8192];
                int r;
                while ((r = bin.read(buffer)) != -1) fout.write(buffer, 0, r);
            }
        }

        // 删除临时文件
        tempBits.delete();
        tempPad.delete();

        // 打印压缩信息
        File outFile = new File(outputPath);
        long outSize = outFile.length();
        double ratio = (double) outSize / (double) originalBytes;
        System.out.printf("压缩完成: 原始 %d bytes, 压缩后 %d bytes, 压缩率 %.2f%%\n",
                originalBytes, outSize, ratio * 100.0);
    }

    // 解压文件
    public static void decompressFile(String inputPath, String outputPath) throws IOException {
        File inFile = new File(inputPath);
        if (!inFile.exists() || !inFile.isFile()) throw new FileNotFoundException("Compressed file not found");

        try (InputStream fin = new BufferedInputStream(new FileInputStream(inFile))) {

            byte[] magic = new byte[4];
            if (fin.read(magic) != 4) throw new IOException("Bad file format (magic)");
            if (!(magic[0] == MAGIC[0] && magic[1] == MAGIC[1] && magic[2] == MAGIC[2] && magic[3] == MAGIC[3])) {
                throw new IOException("Not a Huffman compressed file (magic mismatch)");
            }
            long originalBytes = readLong(fin);
            int uniqueCount = readInt(fin);
            Map<Byte, Long> freqMap = new HashMap<>();
            for (int i = 0; i < uniqueCount; i++) {
                int sym = fin.read();
                if (sym == -1) throw new EOFException("Unexpected EOF while reading header symbol");
                byte b = (byte) sym;
                long freq = readLong(fin);
                freqMap.put(b, freq);
            }
            int paddingByte = fin.read();
            if (paddingByte == -1) throw new EOFException("Unexpected EOF while reading padding");


            Node root = buildHuffmanTree(freqMap);


            try (BitInputStream bis = new BitInputStream(fin);
                 OutputStream out = new BufferedOutputStream(new FileOutputStream(outputPath))) {

                long written = 0;
                Node node = root;
                while (written < originalBytes) {
                    int bit = bis.readBit();
                    if (bit == -1) {
                        throw new EOFException("Unexpected end of bitstream during decode");
                    }
                    node = (bit == 0) ? node.left : node.right;
                    if (node == null) throw new IOException("Decoding error: reached null node");
                    if (node.isLeaf()) {
                        out.write(node.b & 0xFF);
                        written++;
                        node = root;
                    }
                }
                out.flush();
            }
        }
        System.out.println("解压完成，输出到: " + outputPath);
    }

    // 辅助方法：写入 4 字节 int（大端）
    private static void writeInt(OutputStream out, int v) throws IOException {
        out.write((v >>> 24) & 0xFF);
        out.write((v >>> 16) & 0xFF);
        out.write((v >>> 8) & 0xFF);
        out.write(v & 0xFF);
    }

    // 读 4 字节 int（大端）
    private static int readInt(InputStream in) throws IOException {
        int a = in.read();
        int b = in.read();
        int c = in.read();
        int d = in.read();
        if ((a | b | c | d) < 0) throw new EOFException();
        return ((a << 24) | (b << 16) | (c << 8) | d);
    }

    // 写 8 字节 long（大端）
    private static void writeLong(OutputStream out, long v) throws IOException {
        out.write((int) ((v >>> 56) & 0xFF));
        out.write((int) ((v >>> 48) & 0xFF));
        out.write((int) ((v >>> 40) & 0xFF));
        out.write((int) ((v >>> 32) & 0xFF));
        out.write((int) ((v >>> 24) & 0xFF));
        out.write((int) ((v >>> 16) & 0xFF));
        out.write((int) ((v >>> 8) & 0xFF));
        out.write((int) (v & 0xFF));
    }

    // 读 8 字节 long（大端）
    private static long readLong(InputStream in) throws IOException {
        long a = in.read();
        long b = in.read();
        long c = in.read();
        long d = in.read();
        long e = in.read();
        long f = in.read();
        long g = in.read();
        long h = in.read();
        if ((a | b | c | d | e | f | g | h) < 0) throw new EOFException();
        return ((a << 56) | (b << 48) | (c << 40) | (d << 32) |
                (e << 24) | (f << 16) | (g << 8) | h);
    }

    // main: 命令行接口
    public static void main(String[] args) {
        if (args.length < 3) {
            System.out.println("用法:");
            System.out.println("  压缩:   java HuffmanCompress compress input.txt output.huff");
            System.out.println("  解压:   java HuffmanCompress decompress input.huff output.txt");
            return;
        }
        String cmd = args[0];
        String in = args[1];
        String out = args[2];
        try {
            if ("compress".equalsIgnoreCase(cmd)) {
                compressFile(in, out);
            } else if ("decompress".equalsIgnoreCase(cmd)) {
                decompressFile(in, out);
            } else {
                System.err.println("未知命令: " + cmd);
            }
        } catch (Exception e) {
            System.err.println("发生错误: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
