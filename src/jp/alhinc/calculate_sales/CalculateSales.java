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

	public static void main(String[] args) {
		// 支店コードと支店名を保持するMap
		Map<String, String> branchNames = new HashMap<>();
		// 支店コードと売上金額を保持するMap
		Map<String, Long> branchSales = new HashMap<>();

		String branchFile = args[0] + "/" + FILE_NAME_BRANCH_LST;
		try (BufferedReader br = new BufferedReader(new FileReader(branchFile))) {
			String line;
			while ((line = br.readLine()) != null) {
				String[] items = line.split(",");
				if (items.length != 2 || !items[0].matches("\\d{3}")) {
					System.out.println(FILE_INVALID_FORMAT);
					return;
				}
				branchNames.put(items[0], items[1]);
				branchSales.put(items[0], 0L);
			}
		} catch (IOException e) {
			System.out.println(UNKNOWN_ERROR);
			e.printStackTrace();
			return;
		}

		// 売上ファイルの取得
		File dir = new File(args[0]);
		File[] files = dir.listFiles();
		if (files == null) {
			System.out.println("指定したフォルダが見つかりません");
			return;
		}

		List<File> salesFiles = new ArrayList<>();
		for (File file : files) {
			if (file.getName().matches("\\d{8}\\.rcd")) {
				salesFiles.add(file);
			}
		}

		// 売上ファイルの処理
		for (File salesFile : salesFiles) {
			try (BufferedReader br = new BufferedReader(new FileReader(salesFile))) {
				String branchCode = br.readLine(); // 1行目: 支店コード
				String salesAmountStr = br.readLine(); // 2行目: 売上金額

				if (branchCode == null || salesAmountStr == null || !branchCode.matches("\\d{3}")
						|| !salesAmountStr.matches("\\d+")) {
					System.out.println("売上ファイルのフォーマットが不正です: " + salesFile.getName());
					return;
				}

				long salesAmount = Long.parseLong(salesAmountStr);

				if (branchSales.containsKey(branchCode)) {
					branchSales.put(branchCode, branchSales.get(branchCode) + salesAmount);
				} else {
					System.out.println("エラー: 売上ファイルの支店コードが支店定義に存在しません: " + branchCode);
				}
			} catch (IOException e) {
				System.out.println("売上ファイルの読み込み中にエラーが発生しました: " + salesFile.getName());
				e.printStackTrace();
				return;
			}
		}
		// writeFile を呼び出して集計結果を書き込む**
		String outputFile = args[0] + "/" + FILE_NAME_BRANCH_OUT;
		try (BufferedWriter bw = new BufferedWriter(new FileWriter(outputFile))) {
			for (String branchCode : branchNames.keySet()) {
				String branchName = branchNames.get(branchCode);
				Long totalSales = branchSales.get(branchCode);
				bw.write(branchCode + "," + branchName + "," + totalSales);
				bw.newLine();
			}
		} catch (IOException e) {
			System.out.println("支店別集計ファイルの書き込み中にエラーが発生しました");
			e.printStackTrace();
		}
	}
}
