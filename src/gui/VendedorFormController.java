package gui;

import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.util.Callback;
import model.entities.Departamento;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.DepartamentoService;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {

	private Vendedor entity;

	private VendedorService service;

	private DepartamentoService departamentoService;

	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;

	@FXML
	private TextField txtNome;

	@FXML
	private TextField txtEmail;

	@FXML
	private DatePicker DtDeNascimento;

	@FXML
	private TextField txtSalario;

	@FXML
	private ComboBox<Departamento> comboBoxDepartamento;

	@FXML
	private Label labelErrorNome;

	@FXML
	private Label labelErrorEmail;

	@FXML
	private Label labelErrorDtDeNascimento;

	@FXML
	private Label labelErrorSalario;

	@FXML
	private Button btConfirmar;

	@FXML
	private Button brCancelar;

	private ObservableList<Departamento> obsList;

	public void setVendedor(Vendedor entity) {
		this.entity = entity;
	}

	public void setServices(VendedorService service, DepartamentoService departamentoService) {
		this.service = service;
		this.departamentoService = departamentoService;
	}

	public void subscribeDataChangeListener(DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}

	@FXML
	public void onBtConfirmarAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("A entidade está em branco");
		}
		if (service == null) {
			throw new IllegalStateException("O serviço está em branco");
		}

		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListeners();
			Utils.currentStage(event).close();
		} catch (ValidationException e) {
			setErroMessages(e.getErros());
		} catch (DbException e) {
			Alertas.showAlert("Error ao salvar", null, e.getMessage(), AlertType.ERROR);
		}

	}

	private void notifyDataChangeListeners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanged();
		}
	}

	private Vendedor getFormData() {
		Vendedor obj = new Vendedor();

		ValidationException exception = new ValidationException("ERRO ao validar os dados");

		obj.setId(Utils.tryParseToInt(txtId.getText()));

		if (txtNome.getText() == null || txtNome.getText().trim().equals("")) {
			exception.addErro("nome", "O campo não pode está vazio");
		}
		obj.setName(txtNome.getText());
		
		if (txtEmail.getText() == null || txtEmail.getText().trim().equals("")) {
			exception.addErro("email", "O campo não pode está vazio");
		}
		obj.setName(txtEmail.getText());
		
		if (DtDeNascimento.getValue() == null) {
			exception.addErro("dataDeNascimento", "O campo não pode está vazio");
		}
		else {
			Instant instant = Instant.from(DtDeNascimento.getValue().atStartOfDay(ZoneId.systemDefault()));
			obj.setDataDeNascimento(Date.from(instant));
		}
		
		if (txtSalario.getText() == null || txtSalario.getText().trim().equals("")) {
			exception.addErro("salario", "O campo não pode está vazio");
		}
		obj.setSalario(Utils.tryParseToDouble(txtSalario.getText()));
		obj.setDepartment(comboBoxDepartamento.getValue());
		
		if (exception.getErros().size() > 0) {
			throw exception;
		}

		return obj;

	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();

	}

	public void initializeNodes() {
		Restricoes.setTextFieldInteger(txtId);
		Restricoes.setTextFieldMaxLength(txtNome, 30);
		Restricoes.setTextFieldDouble(txtSalario);
		Restricoes.setTextFieldMaxLength(txtEmail, 60);
		Utils.formatDatePicker(DtDeNascimento, "dd/MM/yyyy");
		initializeComboBoxDepartamento();

	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entidade nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getName());
		txtEmail.setText(entity.getEmail());
		Locale.setDefault(Locale.US);
		txtSalario.setText(String.format("%.2f", entity.getSalario()));

		if (entity.getDataDeNascimento() != null) {
			DtDeNascimento.setValue(LocalDate.ofInstant(entity.getDataDeNascimento().toInstant(), ZoneId.systemDefault()));
		}
		
		if (entity.getDepartment() == null) {
			comboBoxDepartamento.getSelectionModel().selectFirst();
		}
		else {
			comboBoxDepartamento.setValue(entity.getDepartment());
		}

	}

	public void loadAssociatedObjects() {
		if (departamentoService == null) {
			throw new IllegalStateException("Departamento de serviços está nulo");
		}
		List<Departamento> list = departamentoService.findAll();
		obsList = FXCollections.observableArrayList(list);
		comboBoxDepartamento.setItems(obsList);
	}

	public void setErroMessages(Map<String, String> erros) {
		Set<String> fields = erros.keySet();

		if (fields.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
		}
		else {
			labelErrorNome.setText("");
		}
		
		if (fields.contains("email")) {
			labelErrorEmail.setText(erros.get("email"));
		}
		else {
			labelErrorEmail.setText("");
		}
		
		if (fields.contains("salario")) {
			labelErrorSalario.setText(erros.get("salario"));
		}
		else {
			labelErrorSalario.setText("");
		}
		
		if (fields.contains("dataDeNascimento")) {
			labelErrorDtDeNascimento.setText(erros.get("dataDeNascimento"));
		}
		else {
			labelErrorDtDeNascimento.setText("");
		}
	}

	private void initializeComboBoxDepartamento() {
		Callback<ListView<Departamento>, ListCell<Departamento>> factory = lv -> new ListCell<Departamento>() {
			@Override
			protected void updateItem(Departamento item, boolean empty) {
				super.updateItem(item, empty);
				setText(empty ? "" : item.getName());
			}
		};
		comboBoxDepartamento.setCellFactory(factory);
		comboBoxDepartamento.setButtonCell(factory.call(null));
	}

}
