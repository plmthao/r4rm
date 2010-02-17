package de.mpicbg.tds.rm.rplugin;

import com.rapidminer.example.Attribute;
import com.rapidminer.example.Example;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.example.table.AttributeFactory;
import com.rapidminer.example.table.DoubleArrayDataRow;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.operator.IOObject;
import com.rapidminer.tools.Ontology;
import org.rosuda.REngine.*;
import org.rosuda.REngine.Rserve.RConnection;
import org.rosuda.REngine.Rserve.RserveException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Document me!
 *
 * @author Holger Brandl
 */
public class RUtils {

	public static RList convert2RList(ExampleSet exampleSet) throws RserveException, REXPMismatchException {

		int numRows = exampleSet.size();


		RList rList = new RList(exampleSet.getAttributes().size(), true);

		for (Attribute attribute : exampleSet.getAttributes()) {
			int counter;
			switch (attribute.getValueType()) {
				case Ontology.STRING:
				case Ontology.DATE:
				case Ontology.NOMINAL:
					String[] strColumn = new String[numRows];

					counter = 0;
					for (Example example : exampleSet) {
						strColumn[counter++] = example.getValueAsString(attribute);
					}

					rList.put(attribute.getName(), new REXPString(strColumn));
					break;

				case Ontology.NUMERICAL:
				case Ontology.INTEGER:
				case Ontology.REAL:
					double[] numColumn = new double[numRows];

					counter = 0;
					for (Example example : exampleSet) {
						numColumn[counter++] = example.getValue(attribute);
					}

					rList.put(attribute.getName(), new REXPDouble(numColumn));
					break;
				default:
					throw new RuntimeException("value type of attribute '" + attribute + "' not mapped");
			}
		}

		return rList;
	}


	public static ExampleSet convert2ExSet(REXP rexp) {

		try {
			RList rList = rexp.asList();
			ArrayList<Attribute> dfAttributes = new ArrayList<Attribute>();
			Map<String, REXPVector> columnData = new HashMap<String, REXPVector>();


			// create attributes
			for (Object columnKey : rList.keySet()) {
				REXPVector column = (REXPVector) rList.get(columnKey);

				assert columnKey instanceof String : " key is not a string";
				String columnName = columnKey.toString();

				Attribute attribute;

				if (column.isNumeric()) {
					attribute = AttributeFactory.createAttribute(columnName, Ontology.NUMERICAL);

				} else if (column.isString()) {
					attribute = AttributeFactory.createAttribute(columnName, Ontology.STRING);

				} else if (column.isLogical()) {
					attribute = AttributeFactory.createAttribute(columnName, Ontology.NOMINAL);
				} else {
					throw new RuntimeException("column type not supported");
				}

				dfAttributes.add(attribute);
				columnData.put(attribute.getName(), column);
			}

			// create examples

//            ArrayList<Attribute> attributes = new ArrayList<Attribute>(metaData.getAllAttributes().size());
//            for (AttributeMetaData amd : metaData.getAllAttributes()) {
//                attributes.add(AttributeFactory.createAttribute(amd.getName(), amd.getValueType()));
//            }
			MemoryExampleTable table = new MemoryExampleTable(dfAttributes);

			int numExamples = columnData.isEmpty() ? -1 : columnData.values().iterator().next().length();
			int numAttributes = columnData.size();

			// create data rows
			for (int i = 0; i < numExamples; i++) {
				table.addDataRow(new DoubleArrayDataRow(new double[numAttributes]));
			}

			// populate datarows
			for (Attribute attribute : table.getAttributes()) {

				REXPVector curColumn = columnData.get(attribute.getName());

				if (Ontology.ATTRIBUTE_VALUE_TYPE.isA(attribute.getValueType(), Ontology.NUMERICAL)) {
					double[] doubleColumn = curColumn.asDoubles();

					for (int i = 0; i < numExamples; i++) {
						table.getDataRow(i).set(attribute, doubleColumn[i]);
					}

				} else if (curColumn.isString()) {
					String[] stringColumn = curColumn.asStrings();

					for (int i = 0; i < numExamples; i++) {
						int mappedNominal = attribute.getMapping().mapString(stringColumn[i]);
						table.getDataRow(i).set(attribute, mappedNominal);
					}
				}

				// todo maybe we have to treat other types here

			}

			return table.createExampleSet();

		} catch (REXPMismatchException e1) {
			throw new RuntimeException();
		}

		// fill table with data

//            predictedLabelAttribute.getMapping().mapString(classification)
		// create example set and return it
//                    e.setValue(predictedLabelAttribute, predictedLabel);

	}


	public static String prepare4RExecution(String script) {
		script = script.replace("\r", " ").replace("\n", " ").replace("\t", " ");

		if (!script.endsWith(";")) {
			script += ";";
		}

		return script;
	}


	public static RConnection createConnection() {

		String host = getHost();
		int port = Integer.parseInt(System.getProperty(PluginInitializer.R_SERVE_PORT, PluginInitializer.R_SERVE_PORT_DEFAULT + ""));

		try {
			return new RConnection(host, port);
		} catch (RserveException e) {
			throw new RuntimeException(e);
		}
	}


	public static String getHost() {
		return System.getProperty(PluginInitializer.R_SERVE_HOST, PluginInitializer.R_SERVE_HOST_DEFAULT);
	}


	public static Map<String, IOObject> push2R(RConnection connection, List<IOObject> inputs, ArrayList<String> parNames) throws RserveException, REXPMismatchException {

		if (parNames != null) {
			if (parNames.size() != inputs.size()) {
				throw new RuntimeException("Number of r-variable names does not match the number of input tables");
			}
		} else {
			parNames = new ArrayList<String>();

			for (int i = 0; i < inputs.size(); i++) {
				parNames.add("in" + (i + 1));
			}
		}

		Map<String, IOObject> pushTable = new HashMap<String, IOObject>();
		for (int i = 0; i < inputs.size(); i++) {
			IOObject input = inputs.get(i);
			pushTable.put(parNames.get(i), input);
		}

		return push2R(connection, pushTable);
	}


	public static Map<String, IOObject> push2R(RConnection connection, Map<String, IOObject> pushTable) throws RserveException, REXPMismatchException {
		for (String parName : pushTable.keySet()) {
			IOObject ioObject = pushTable.get(parName);

			if (!(ioObject instanceof ExampleSet))
				throw new RuntimeException();

			ExampleSet exampleSet = (ExampleSet) ioObject;

			RList inputAsRList = convert2RList(exampleSet);
			connection.assign(parName, REXP.createDataFrame(inputAsRList));
		}

		return pushTable;
	}
}
