package com.example.fileio

import java.io.*

/*File() 클래스에 디렉터리를 매개변수로 전달한 후 사용
isFile() :  File 의 생성자에 전달된 경로가 '파일'인지 확인
isDirectory() : File 의 생성자에 전달된 경로가 '디텍터리'인지 확인
exists() : 파일 존재 여부 확인. if 문과 함께 사용
createNewFile() : 해당 디렉터리에 파일이 존재 하지 않으면 파일을 생성. exists()와 함께 사용
mkdirs() : 중간 디렉터리가 없다면 중간 디렉터리도 생성. exists()와 함께 사용
*/

class FileUtil {
    /*
    FileWriter() : 쓰기 스트림으로 디렉터리를 생성 ex)/data/user/0/com.example.fileio/files/NewFile.txt
    BufferedWriter() : 쓰기 스트림을 매개변수로 받아 버퍼를 생성해 저장 속도를 향상 시킴
    사용이 끝난 버퍼는 닫아 메모리 누수를 최소화 시켜줘야함
    */
    fun writeTextFile(directory:String, filename:String, content:String){
        val dir = File(directory)
        if(!dir.exists()){
            dir.mkdirs()
        }

        val writer = FileWriter(directory + "/" + filename)
        val buffer = BufferedWriter(writer)
        buffer.write(content)
        buffer.close()
    }

    /*
    FileReader() : 읽기 스트림으로 읽어올 데이터의 경로를 갖고 있음
    BufferedReader() : 읽기 스트림을 매개변수로 받아 버퍼를 생성해 읽기 속도를 향상 시킴
    사용이 끝난 버퍼는 닫아 메모리 누수를 최소화 시켜줘야함

    참고 : openFileInput() 메서드를 사용하면 더 짧은 코드로 텍스트 파일을 읽을 수 있음
    var contents = ""
    context.openFileInput("파일 경로").bufferedReader().useLines { lines ->
          contents = lines.joinToString("\n")
    }
    */
    fun readTextFile(fullPath:String) : String {
        val file = File(fullPath)
        if(!file.exists()) return ""

        val reader = FileReader(file)
        val buffer = BufferedReader(reader)
        var temp:String? = null
        var result = ""
        /*
        buffer 를 통해 한 줄씩 읽은 데이터를 임시로 temp 에 저장
        temp 에 저장된 데이터를 result 로 최종 저장
        */
        while(true){
            temp = buffer.readLine()
            if(temp == null) break
            else result += temp + "\n"
        }
        buffer.close()
        reader.close()

        return result
    }

    fun isDir(fullPath:String) : Boolean {
        val file = File(fullPath)
        return file.isDirectory
    }

    fun isExist(fullPath:String) : Boolean {
        val file = File(fullPath)
        return file.exists()
    }

    fun getName(fullPath:String) : String {
        val file = File(fullPath)
        return file.name
    }

    fun isFile(fullPath:String) : Boolean {
        val file = File(fullPath)
        return file.isFile
    }
}