package com.mycompany.quanlydoituongdacbiet.utils; // gói chứa các lớp tiện ích

import java.io.File; // đại diện cho một file trên ổ đĩa
import javax.xml.bind.JAXBContext; // ngữ cảnh JAXB: biết cách chuyển lớp Java <-> XML
import javax.xml.bind.JAXBException; // lỗi do JAXB ném ra
import javax.xml.bind.Marshaller; // công cụ chuyển object -> XML
import javax.xml.bind.Unmarshaller; // công cụ chuyển XML -> object

public class FileUtils { // lớp chứa các hàm tĩnh đọc/ghi file XML

    /** Chuyển object thành XML rồi lưu vào fileName. */
    public static void writeXMLtoFile(String fileName, Object object) { // hàm ghi: nhận tên file và đối tượng cần ghi
        try { // thử thực hiện, nếu lỗi nhảy xuống catch
            JAXBContext ctx = JAXBContext.newInstance(object.getClass()); // tạo ngữ cảnh JAXB theo kiểu của object
            Marshaller m = ctx.createMarshaller(); // tạo bộ chuyển object -> XML
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // bật định dạng thụt lề cho XML dễ đọc
            m.setProperty(Marshaller.JAXB_ENCODING, "UTF-8"); // ghi bằng UTF-8 để không lỗi tiếng Việt
            m.marshal(object, new File(fileName)); // ghi object ra file
        } catch (JAXBException e) { // bắt lỗi JAXB
            e.printStackTrace(); // in lỗi ra console
        } // kết thúc catch
    } // kết thúc hàm ghi

    /** Đọc fileName và chuyển thành object kiểu clazz; trả null nếu file chưa có/lỗi. */
    public static Object readXMLFile(String fileName, Class<?> clazz) { // hàm đọc: nhận tên file và kiểu đối tượng mong muốn
        File xmlFile = new File(fileName); // tạo đối tượng File trỏ tới đường dẫn
        if (!xmlFile.exists()) { // nếu file chưa tồn tại (lần chạy đầu tiên)
            return null; // trả null để nơi gọi tạo danh sách rỗng
        } // kết thúc if
        try { // thử đọc file
            JAXBContext ctx = JAXBContext.newInstance(clazz); // tạo ngữ cảnh JAXB theo lớp clazz
            Unmarshaller u = ctx.createUnmarshaller(); // tạo bộ chuyển XML -> object
            return u.unmarshal(xmlFile); // đọc file và trả về object tương ứng
        } catch (JAXBException e) { // bắt lỗi (file sai định dạng...)
            e.printStackTrace(); // in lỗi ra console
        } // kết thúc catch
        return null; // nếu lỗi thì trả null
    } // kết thúc hàm đọc
} // kết thúc lớp
