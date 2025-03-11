package jp.alhinc.calculate_sales;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CalculateSales {

	// 支店定義ファイル名
	private static final String FILE_NAME_BRANCH_LST = "branch.lst";

	// 支店別集計ファイル名
	private static final String FILE_NAME_BRANCH_OUT = "branch.out";

	// エラーメッセージ
	private static final String UNKNOWN_ERROR = "予期せぬエラーが発生しました";
	private static final String FILE_NOT_EXIST = "支店定義ファイルが存在しません";
	private static final String FILE_INVALID_FORMAT = "支店定義ファイルのフォーマットが不正です";

	/**
	 * メインメソッド
	 *
	 * @param コマンドライン引数
	 */
	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		// 支店定義ファイル読み込み処理
		if (!readFile(args[0], FILE_NAME_BRANCH_LST, branchNames, branchSales)) {
			return;
		}
		
		List<File> salesFiles = readSalesFiles(args[0]); //売上フォルダを探している
		
		for (File salesFile : salesFiles) {
            processSalesFile(salesFile, branchSales);
            
        }
		    
		    if (!writeFile(args[0], FILE_NAME_BRANCH_OUT, branchNames, branchSales)) {
	            return;
		    }
	}
	
	private static List<File> readSalesFiles(String path) {
		List<File> rcdFiles = new ArrayList<>(); // 売上ファイルを格納するリスト
		File[] files = new File(path).listFiles(); // 指定フォルダ内の全ファイル取得
		
		if (files == null) {
			 return rcdFiles; //見つけた売上ファイルをリストに追加して返す
		}
		
		for(int i = 0; i < files.length; i++) { 
		      String fileName = files[i].getName(); // **ファイル名を取得**
		      
				 if(fileName.matches("\\d{8}\\.rcd")) { 
			            rcdFiles.add(files[i]); 	
		    }
		}
		
		return rcdFiles; // 売上ファイルのリストを返す
	}
	
	private static void processSalesFile(File salesFile, Map<String, Long> branchSales) {
		
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(salesFile));
			
			 String branchCode = br.readLine();  // 売上ファイルの1行目：支店コード
			 String salesAmountStr = br.readLine();// 売上ファイルの2行目：売上金額
			 
			 if (branchCode == null || salesAmountStr == null || !branchCode.matches("\\d{3}") || !salesAmountStr.matches("\\d+")) {
		            return;
		        }
			 
			 long salesAmount = Long.parseLong(salesAmountStr);
			 
			 if (branchSales.containsKey(branchCode)) {
		            branchSales.put(branchCode, branchSales.get(branchCode) + salesAmount);
		        } else {
		        }
			 
		 } catch (IOException e) {
		        e.printStackTrace();
		    } finally {
		        try {
		            if (br != null) br.close();
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
		    }
			 
		}
	
	private static boolean readFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		 BufferedReader br = null;
	        try {
	            File file = new File(path, fileName);
	            if (!file.exists()) {
	            	 System.out.println(FILE_NOT_EXIST);
	                return false;
	            }
	            br = new BufferedReader(new FileReader(file));

	            String line;
	            while ((line = br.readLine()) != null) {
	                String[] items = line.split(",");
	                if (items.length != 2 || !items[0].matches("\\d{3}")) {
	                	System.out.println(FILE_INVALID_FORMAT);
	                    return false;
	                }
	                branchNames.put(items[0], items[1]);
	                branchSales.put(items[0], 0L);
	            }

	            return true;
	        } catch (IOException e) {
	        	 System.out.println(UNKNOWN_ERROR);
	            e.printStackTrace();
	            return false;
	        } finally {
	            try {
	                if (br != null) br.close();
	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        }
	    }

	private static boolean writeFile(String path, String fileName, Map<String, String> branchNames, Map<String, Long> branchSales) {
		 File file = new File(path, fileName);
		 
		 try (BufferedWriter bw = new BufferedWriter(new FileWriter(file))) {
			 for (String branchCode : branchNames.keySet()) {
		            String branchName = branchNames.get(branchCode);
		            Long totalSales = branchSales.get(branchCode);
		            
		            bw.write(branchCode + "," + branchName + "," + totalSales);
		            bw.newLine(); // 改行
		 }
	} catch (IOException e) {
		        e.printStackTrace();
		        return false;
	}
		
        return true;
	}
}
		// ※ここから集計処理を作成してください。(処理内容2-1、2-2)

	/**
	 * 支店定義ファイル読み込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 読み込み可否
	 */
	

	/**
	 * 支店別集計ファイル書き込み処理
	 *
	 * @param フォルダパス
	 * @param ファイル名
	 * @param 支店コードと支店名を保持するMap
	 * @param 支店コードと売上金額を保持するMap
	 * @return 書き込み可否
	 */

