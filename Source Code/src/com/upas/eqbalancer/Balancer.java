package com.upas.eqbalancer;

import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;


public class Balancer extends Activity implements View.OnClickListener {
	/** Called when the activity is first created. */
	private static final int MENUABOUT = Menu.FIRST;
	private static final int MENUHELP = Menu.FIRST + 1;
	private static final int MENUQUIT = Menu.FIRST + 2;
	TextView ans;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		defaultops();
	}
	public void defaultops(){
		ans = (TextView)findViewById(R.id.TextView01);
		ans.setTextColor(Color.CYAN);
		ans.setTextSize(18);
		TextView disp = (TextView)findViewById(R.id.TextView02);
		TextView disp1 = (TextView)findViewById(R.id.TextView03);
		disp.setTextSize(14);
		disp1.setTextSize(14);
		Button b = (Button)findViewById(R.id.Button01);
		Button b1 = (Button)findViewById(R.id.Button02);
		b.setOnClickListener(this);
		b1.setOnClickListener(clearClick);
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
			WebView w = (WebView)findViewById(R.id.WebView01);
			w.loadDataWithBaseURL("file:///android_asset/", "<img src='elements.jpg' />", "text/html", "utf-8", null);
			w.getSettings().setBuiltInZoomControls(true);
			w.setBackgroundColor(Color.BLACK);
		}
		else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
			Button b2 = (Button)findViewById(R.id.Button03);
			b2.setOnClickListener(ptClick);
		}
	}
	public OnClickListener ptClick = new OnClickListener() {
		public void onClick(View view) {
			setContentView(R.layout.custom_dialog);
			WebView w1 = (WebView)findViewById(R.id.WebViewDiag);
			w1.loadDataWithBaseURL("file:///android_asset/", "<img src='elements.jpg' />", "text/html", "utf-8", null);
			w1.getSettings().setBuiltInZoomControls(true);
			w1.setBackgroundColor(Color.BLACK);
			Button b3 = (Button)findViewById(R.id.ButtonDiag);
			b3.setOnClickListener(new OnClickListener(){
				public void onClick(View view){
					setContentView(R.layout.main);
					defaultops();
				}
			});
		}
	};
	public OnClickListener clearClick = new OnClickListener() {
		public void onClick(View view) {
			EditText equ = (EditText)findViewById(R.id.EditText01);
			equ.setText("");
			ans = (TextView)findViewById(R.id.TextView01);
			ans.setText("");
		}
	};
	public void onClick(View view){
		EditText equ = (EditText)findViewById(R.id.EditText01);
		String equation = equ.getText().toString();
		String equation1 = removeSpaces(equation);
		ArrayList<String> rterms = new ArrayList<String>();
		ArrayList<String> pterms = new ArrayList<String>();
		ArrayList<Double> coeffsr = new ArrayList<Double>();
		ArrayList<Double> coeffsp = new ArrayList<Double>();
		try{
			String reactants = equation.substring(0, equation.indexOf("="));
			String products = equation.substring(equation.indexOf("=") + 1, equation.length());
			reactants = reactants.trim();
			products = products.trim();
			converttoArrayList(reactants, rterms);
			converttoArrayList(products, pterms);
			String rterms1[] = new String[rterms.size()];
			String pterms1[] = new String[pterms.size()];
			for (int i = 0; i < rterms.size(); i++){
				if (Character.isDigit(rterms.get(i).charAt(0))){
					throw new Exception();
				}
				rterms.set(i, rterms.get(i).trim());
				rterms1[i] = rterms.get(i);
			}
			for (int i = 0; i < pterms.size(); i++){
				if (Character.isDigit(pterms.get(i).charAt(0))){
					throw new Exception();
				}
				pterms.set(i, pterms.get(i).trim());
				pterms1[i] = pterms.get(i);
			}
			boolean isredox = false;
			for (int i = 0; i < rterms.size(); i++){
				String x = rterms.get(i);
				for (int j = 0; j < x.length(); j++){
					if (j == x.length() - 1 && x.charAt(j) != ']'){
						coeffsr.add(0.0);
					}
					if (x.charAt(j) == '['){
						isredox = true;
						int ind = x.indexOf(']');
						String num = x.substring(j + 1, ind);
						coeffsr.add(Double.parseDouble(num));
						x = x.substring(0, j);
						break;
					}
				}
				rterms.set(i, x);
			}
			for (int i = 0; i < pterms.size(); i++){
				String x = pterms.get(i);
				for (int j = 0; j < x.length(); j++){
					if (j == x.length() - 1 && x.charAt(j) != ']'){
						coeffsp.add(0.0);
					}
					if (x.charAt(j) == '['){
						isredox = true;
						int ind = x.indexOf(']');
						String num = x.substring(j + 1, ind);
						coeffsp.add(Double.parseDouble(num));
						x = x.substring(0, j);
						break;
					}
				}
				pterms.set(i, x);
			}
			addNums(rterms);
			configureParenthesis(rterms);
			addNums(pterms);
			configureParenthesis(pterms);
			addNums(rterms);
			addNums(pterms);
			int count = 0;
			int size = rterms.size() + pterms.size();
			String h[] = new String[1000];
			ArrayList <String> elements = new ArrayList<String>();
			for (int i = 0; i < rterms.size(); i++){
				Pattern p1 = Pattern.compile("([A-Z])(\\d+)");
				String comp = rterms.get(i);
				Matcher m1 = p1.matcher(comp);
				while (m1.find()) {
					if (!elements.contains(m1.group(1)))
					{
						elements.add(m1.group(1));
					}
				}
				Pattern p2 = Pattern.compile("([A-Z])([a-z])(\\d+)");
				Matcher m2 = p2.matcher(comp);
				while (m2.find()) {
					if (!elements.contains(m2.group(1) + "" + m2.group(2)))
					{
						elements.add(m2.group(1) + "" + m2.group(2));
					}
				}
				Pattern p3 = Pattern.compile("([A-Z])([a-z])([a-z])(\\d+)");
				Matcher m3 = p3.matcher(comp);
				while (m3.find()) {
					if (!elements.contains(m3.group(1) + "" + m3.group(2) + "" + m3.group(3)))
					{
						elements.add(m3.group(1) + "" + m3.group(2) + "" + m3.group(3));
					}
				}
			}
			for (int i = 0; i < pterms.size(); i++){
				Pattern p1 = Pattern.compile("([A-Z])(\\d+)");
				String comp = pterms.get(i);
				Matcher m1 = p1.matcher(comp);
				while (m1.find()) {
					if (!elements.contains(m1.group(1)))
					{
						elements.add(m1.group(1));
					}
				}
				Pattern p2 = Pattern.compile("([A-Z])([a-z])(\\d+)");
				Matcher m2 = p2.matcher(comp);
				while (m2.find()) {
					if (!elements.contains(m2.group(1) + "" + m2.group(2)))
					{
						elements.add(m2.group(1) + "" + m2.group(2));
					}
				}
				Pattern p3 = Pattern.compile("([A-Z])([a-z])([a-z])(\\d+)");
				Matcher m3 = p3.matcher(comp);
				while (m3.find()) {
					if (!elements.contains(m3.group(1) + "" + m3.group(2) + "" + m3.group(3)))
					{
						elements.add(m3.group(1) + "" + m3.group(2) + "" + m3.group(3));
					}
				}
			}
			for (int i = 0; i < rterms.size(); i++){
				if (rterms.get(i).length() == 1){
					String e = rterms.get(i);
					rterms.set(i, e + "1");
				}
			}
			for (int i = 0; i < pterms.size(); i++){
				if (pterms.get(i).length() == 1){
					String e = pterms.get(i);
					pterms.set(i, e + "1");
				}
			}
			double m[][] = null;
			int track = 0;
			int rows = elements.size();
			if (size > rows){
				m = new double[size][size];
			}
			else if (rows > size){
				m = new double[rows][size];
			}
			else if (rows == size){
				m = new double[size][rows];
			}
			if (isredox){
				m = new double[m.length + 1][m[0].length];
				int c = 0;
				for (int i = 0; i < coeffsr.size(); i++){
					m[m.length - 1][c] = coeffsr.get(i);
					c++;
				}
				for (int i = 0; i < coeffsp.size(); i++){
					if (i == coeffsp.size() - 1){
						m[m.length - 1][c] = coeffsp.get(i);
					}
					else
					{
						m[m.length - 1][c] = coeffsp.get(i) * -1;
						c++;
					}
				}
			}
			for (int i = 0; i < rterms.size(); i++){
				ArrayList <String> x = new ArrayList<String>();
				ArrayList <Integer> y = new ArrayList<Integer>();
				Pattern p1 = Pattern.compile("([A-Z])(\\d+)");
				String comp = rterms.get(i);
				Matcher m1 = p1.matcher(comp);
				while (m1.find()) {
					if (x.contains(m1.group(1))){
						int ind = x.indexOf(m1.group(1));
						Integer add = y.get(ind);
						y.set(ind, add + Integer.parseInt(m1.group(2)));
					}
					else
					{
						x.add(m1.group(1));
						y.add(Integer.parseInt(m1.group(2)));
					}
				}
				Pattern p2 = Pattern.compile("([A-Z])([a-z])(\\d+)");
				Matcher m2 = p2.matcher(comp);
				while (m2.find()) {
					if (x.contains(m2.group(1) + "" + m2.group(2))){
						int ind = x.indexOf(m2.group(1) + "" + m2.group(2));
						Integer add = y.get(ind);
						y.set(ind, add + Integer.parseInt(m2.group(3)));
					}
					else {
						x.add(m2.group(1) + "" + m2.group(2));
						y.add(Integer.parseInt(m2.group(3)));
					}
				}
				Pattern p3 = Pattern.compile("([A-Z])([a-z])([a-z])(\\d+)");
				Matcher m3 = p3.matcher(comp);
				while (m3.find()) {
					if (x.contains(m3.group(1) + "" + m3.group(2) + "" + m3.group(3))){
						int ind = x.indexOf(m3.group(1) + "" + m3.group(2)+ "" + m3.group(3));
						Integer add = y.get(ind);
						y.set(ind, add + Integer.parseInt(m3.group(4)));
					}
					else {
						x.add(m3.group(1) + "" + m3.group(2) + "" + m3.group(3));
						y.add(Integer.parseInt(m3.group(4)));
					}
				}
				if (i == 0){
					for (int j = 0; j < y.size(); j++){
						m[track][i] = y.get(j);
						track++;
						h[count] = x.get(j);
						count++;
					}
				}
				else
				{
					boolean b = false;
					for (int j = 0; j < x.size(); j++){
						b = false;
						for (int k = 0; k < h.length; k++){
							if (x.get(j).equals(h[k])){
								m[k][i] = y.get(j);
								b = true;
							}
						}
						if (!b){
							m[track][i] = y.get(j);
							track++;
							h[count] = x.get(j);
							count++;
						}
					}
				}
			}
			for (int i = 0; i < pterms.size(); i++){
				ArrayList <String> x = new ArrayList<String>();
				ArrayList <Integer> y = new ArrayList<Integer>();
				Pattern p1 = Pattern.compile("([A-Z])(\\d+)");
				String comp = pterms.get(i);
				Matcher m1 = p1.matcher(comp);
				while (m1.find()) {
					if (x.contains(m1.group(1))){
						int ind = x.indexOf(m1.group(1));
						Integer add = y.get(ind);
						y.set(ind, add + Integer.parseInt(m1.group(2)));
					}
					else
					{
						x.add(m1.group(1));
						y.add(Integer.parseInt(m1.group(2)));
					}
				}
				Pattern p2 = Pattern.compile("([A-Z])([a-z])(\\d+)");
				Matcher m2 = p2.matcher(comp);
				while (m2.find()) {
					if (x.contains(m2.group(1) + "" + m2.group(2))){
						int ind = x.indexOf(m2.group(1) + "" + m2.group(2));
						Integer add = y.get(ind);
						y.set(ind, add + Integer.parseInt(m2.group(3)));
					}
					else {
						x.add(m2.group(1) + "" + m2.group(2));
						y.add(Integer.parseInt(m2.group(3)));
					}
				}
				Pattern p3 = Pattern.compile("([A-Z])([a-z])([a-z])(\\d+)");
				Matcher m3 = p3.matcher(comp);
				while (m3.find()) {
					if (x.contains(m3.group(1) + "" + m3.group(2) + "" + m3.group(3))){
						int ind = x.indexOf(m3.group(1) + "" + m3.group(2)+ "" + m3.group(3));
						Integer add = y.get(ind);
						y.set(ind, add + Integer.parseInt(m3.group(4)));
					}
					else {
						x.add(m3.group(1) + "" + m3.group(2) + "" + m3.group(3));
						y.add(Integer.parseInt(m3.group(4)));
					}
				}
				boolean b = false;
				for (int j = 0; j < x.size(); j++){
					b = false;
					for (int k = 0; k < h.length; k++){
						if (x.get(j).equals(h[k])){
							if (i + rterms.size() == m[0].length - 1){
								m[k][i + rterms.size()] = y.get(j);
								b = true;
							}
							else
							{
								m[k][i + rterms.size()] = y.get(j) * -1;
								b = true;
							}
						}
					}
					if (!b){
						if (i + rterms.size() == m[0].length - 1){
							m[track][i + rterms.size()] = y.get(j);
							track++;
							h[count] = x.get(j);
							count++;
						}
						else
						{
							m[track][i + rterms.size()] = y.get(j) * -1;
							track++;
							h[count] = x.get(j);
							count++;
						}
					}
				}
			}
			toRREF(m);
			ArrayList<Double>coefficients = new ArrayList<Double>();
			for (int i = 0; i < m[0].length; i++){
				if (m[i][m[0].length - 1] == 0.0){
					m[i][m[0].length - 1] = 1.0;
				}
				coefficients.add(m[i][m[0].length - 1]);
			}
			Double elem[] = new Double[coefficients.size()];
			int factor = 0;
			int denoms[] = new int[elem.length];
			for (int i = 0; i < elem.length; i++){
				elem[i] = m[i][elem.length - 1];
				denoms[i] = toFraction(elem[i]);
			}
			factor = lcm(denoms);
			int fin[] = new int[elem.length];
			for (int i = 0; i < elem.length; i++){
				elem[i] *= factor;
				fin[i] = (int)Math.round(elem[i].doubleValue());
			}
			String newequ = "";
			String newequ1 = "";
			int cou = 0;
			for (int i = 0; i < rterms1.length; i++){
				if (fin[cou] == 1){
					if (i == rterms1.length - 1){
						newequ += rterms1[i] + " \u2192 ";
						newequ1 += rterms1[i] + " = ";
					}
					else {
						newequ += rterms1[i] + " + ";
						newequ1 += rterms1[i] + " + ";
					}
					cou++;
				}
				else
				{
					if (i == rterms1.length - 1){
						newequ += fin[cou] + rterms1[i] + " \u2192 ";
						newequ1 += fin[cou] + rterms1[i] + " = ";
					}
					else {
						newequ += fin[cou] + rterms1[i] + " + ";
						newequ1 += fin[cou] + rterms1[i] + " + ";
					}
					cou++;
				}
			}
			for (int i = 0; i < pterms1.length; i++){
				if (fin[cou] == 1){
					if (i == pterms1.length - 1) {
						newequ += pterms1[i];
						newequ1 += pterms1[i];
					}
					else {
						newequ += pterms1[i] + " + ";
						newequ1 += pterms1[i] + " + ";
					}
					cou++;
				}
				else
				{
					if (i == pterms1.length - 1){
						newequ += fin[cou] + pterms1[i];
						newequ1 += fin[cou] + pterms1[i];
					}
					else {
						newequ += fin[cou] + pterms1[i] + " + ";
						newequ1 += fin[cou] + pterms1[i] + " + ";
					}
					cou++;
				}
			}
			String newequ2 = removeSpaces(newequ1);
			if (newequ2.equals(equation1)){
				throw new Exception();
			}
			ans.setText(newequ);
		}
		catch (Exception e){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("Error!");
			builder.setMessage("The equation is already balanced or an error occured in computing the balanced equation. Please retype the equation above.");
			builder.setPositiveButton("Close", null);
			builder.setCancelable(true);
			builder.create().show();
		}
	}
	public String removeSpaces(String s) {
		StringTokenizer st = new StringTokenizer(s," ",false);
		String t="";
		while (st.hasMoreElements()) t += st.nextElement();
		return t;
	}
	private static int gcd(int a, int b)
	{
		while (b > 0)
		{
			int temp = b;
			b = a % b;
			a = temp;
		}
		return a;
	}

	public static int lcm(int a, int b)
	{
		return a * (b / gcd(a, b));
	}

	public static int lcm(int[] input)
	{
		int result = input[0];
		for(int i = 1; i < input.length; i++) result = lcm(result, input[i]);
		return result;
	}
	public static int toFraction(double decimal) {
		int LIMIT = 12;
		int denominators[] = new int[LIMIT + 1];
		int numerator, denominator = 0, temp;
		int MAX_GOODNESS = 100;
		int i = 0;
		while (i < LIMIT + 1) {
			denominators[i] = (int) decimal;
			decimal = 1.0 / (decimal - denominators[i]);
			i = i + 1;
		}
		int last = 0;
		while (last < LIMIT) {
			numerator = 1;
			denominator = 1;
			temp = 0;
			int current = last;
			while (current >= 0) {
				denominator = numerator;
				numerator = (numerator * denominators[current]) + temp;
				temp = denominator;
				current = current - 1;
			}
			last = last + 1;
			int goodness = denominators[last];
			if (Math.abs(goodness) > MAX_GOODNESS) break;
		}
		return denominator;
	}

	public static void converttoArrayList(String a, ArrayList <String> b){
		int pos = 0;
		for (int i = 0; i < a.length(); i++){
			if (i == a.length() - 1){
				String x = a.substring(pos, a.length());
				b.add(x);
			}
			if (Character.toString(a.charAt(i)).equals("+")){
				String x = a.substring(pos, i);
				pos = i + 1;
				b.add(x);
			}
		}
	}
	public static void addNums(ArrayList<String>b){
		for (int i = 0; i < b.size(); i++){
			String x = b.get(i);
			for (int j = 0; j < x.length() - 1; j++){
				if (!Character.isDigit(x.charAt(j)) && x.charAt(j + 1) == ')'){
					x = x.substring(0, j + 1) + "1" + x.substring(j + 1, x.length());
					break;
				}
				if ((Character.isUpperCase(x.charAt(j)) && !Character.isDigit(j + 1) && Character.isUpperCase(x.charAt(j + 1)))){
					x = x.substring(0, j + 1) + "1" + x.substring(j + 1, x.length());
				}
				else if (j == x.length() - 2 && Character.isUpperCase(x.charAt(j + 1))){
					x = x + "1";
				}
				if (Character.isUpperCase(x.charAt(j)) && Character.isLowerCase(x.charAt(j + 1))){
					if (j != x.length() - 2){
						if (Character.isUpperCase(x.charAt(j + 2)) || x.charAt(j + 2) == '('){
							x = x.substring(0, j + 2) + "1" + x.substring(j + 2, x.length());
						}
					}
					else if (j == x.length() - 2){
						x = x + "1";
					}
				}
			}
			b.set(i, x);
		}
	}
	public static void configureParenthesis(ArrayList<String>b){
		int oldlength = 0;
		for (int i = 0; i < b.size(); i++){
			String x = b.get(i);
			oldlength = x.length();
			for (int j = 0; j < x.length() - 1; j++){
				if (x.charAt(j) == '('){
					int end = x.indexOf(')');
					Integer factor = Integer.parseInt(Character.toString(x.charAt(end + 1)));
					for (int k = j + 1; k < end; k++){
						if (Character.isDigit(x.charAt(k))){
							Integer num = Integer.parseInt(Character.toString(x.charAt(k)));
							Integer newnum = num * factor;
							String num1 = num.toString();
							int ind = x.indexOf(num1, k);
							x = x.substring(0, ind) + newnum.toString() + x.substring(ind + 1, x.length());
							if (x.length() > oldlength){
								k += x.length() - oldlength;
								end += x.length() - oldlength;
							}
							oldlength = x.length();
						}
					}
					if (j == 0){
						end = x.indexOf(')');
						x = x.substring(1, end) + x.substring(end + 2, x.length());
						b.set(i, x);
						break;
					}
					else
					{
						end = x.indexOf(')');
						x = x.substring(0, j) + x.substring(j + 1, end);
						b.set(i, x);
						break;
					}

				}
			}
		}
	}
	public static void toRREF(double[][] M) {
		int rowCount = M.length;
		if (rowCount == 0)
			return;

		int columnCount = M[0].length;

		int lead = 0;
		for (int r = 0; r < rowCount; r++) {
			if (lead >= columnCount)
				break;
			{
				int i = r;
				while (M[i][lead] == 0) {
					i++;
					if (i == rowCount) {
						i = r;
						lead++;
						if (lead == columnCount)
							return;
					}
				}
				double[] temp = M[r];
				M[r] = M[i];
				M[i] = temp;
			}

			{
				double lv = M[r][lead];
				for (int j = 0; j < columnCount; j++)
					M[r][j] /= lv;
			}

			for (int i = 0; i < rowCount; i++) {
				if (i != r) {
					double lv = M[i][lead];
					for (int j = 0; j < columnCount; j++)
						M[i][j] -= lv * M[r][j];
				}
			}
			lead++;
		}
	}
	public void dislayPopup(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("About");
		builder.setMessage("Chemical Equation Balancer v1.8, by Upas Narayan");
		builder.setPositiveButton("Close", null);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.about);
		builder.create().show();
	}
	public void displayHelp(){
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setTitle("Help");
		builder.setMessage("This chemical equation balancer can balance many types of reactions, " +
				"like single replacement, double replacement, synthesis, decomposition, combustion, and redox reactions. " +
				"In order to get your balanced equation, please enter each compound CAPITALIZED and separated" +
				" by a + sign and an = sign to show the other side. This balancer also supports" +
				" polyatomic compounds, which can be entered with parentheses ( ). For an equation containing polyatomic ions, if there is only one molecule of the ion, such as in H(NO3), DO NOT insert the parentheses, and instead input HNO3. Redox reactions can be typed" +
				" with brackets, [ ] for the charge of the compound. " +
				"For a redox equation, make sure that every compound has a bracket with a charge, even if a compound has no charge." + " For entering a positive charge," +
				" leave off the + sign, but add - for negative charges. For a positive charge, for example, input [2], for a negative charge, [-2], and for a charge of 0, [0]. DO NOT INPUT [+2]. When entering the equation, " +
				"make sure that the first letter of each element is capitalized. For example, instead of ca and h, Ca and H should be entered. " +
				"\n\nThis application operates offline, without the use of the Internet, by solely using mathematical algorithms to find coefficients of compounds." +
				"\n\nSample equations:" + "\nC6H12O6+O2=CO2+H2O" + "\nH2+O2=H2O" +
				"\nAgI+Pb(NO3)2=AgNO3+PbI2" + "\nCa(OH)2+(NH4)3PO4=Ca3(PO4)2+NH4OH" +
		"\nCr2O7[-2]+H[1]+Fe[2]=Cr[3]+H2O+Fe[3]" + "\n" + "\nNOTE: THIS APPLICATION DOES NOT SOLVE EQUATIONS. IT ONLY BALANCES COMPLETE EQUATIONS.");
		builder.setPositiveButton("Close", null);
		builder.setCancelable(true);
		builder.setIcon(R.drawable.help);
		builder.create().show();
	}
	public boolean onCreateOptionsMenu(Menu menu){
		menu.add(0, MENUABOUT, 0, "About");
		menu.add(0, MENUHELP, 0, "Help");
		menu.add(0, MENUQUIT, 0, "Quit");
		return true;
	}
	public boolean onOptionsItemSelected(MenuItem item){
		switch(item.getItemId()){
		case MENUABOUT:
			dislayPopup();
			return true;
		case MENUHELP:
			displayHelp();
			return true;
		case MENUQUIT:
			finish();
			return true;
		}
		return false;
	}
}