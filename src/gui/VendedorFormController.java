package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listeners.DataChangeListener;
import gui.util.Alertas;
import gui.util.Restricoes;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Vendedor;
import model.exceptions.ValidationException;
import model.services.VendedorService;

public class VendedorFormController implements Initializable {
	
	private Vendedor entity;
	
	private VendedorService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();

	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtNome;
	
	@FXML
	private Label labelErrorNome;
	
	@FXML
	private Button btConfirmar;
	
	@FXML
	private Button brCancelar;
	
	public void setVendedor(Vendedor entity) {
		this.entity = entity;
	}
	
	public void setVendedorService(VendedorService service) {
		this.service = service;
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
		}
		catch (ValidationException e) {
			setErroMessages(e.getErros());
		}
		catch (DbException e) {
			Alertas.showAlert("Error ao salvar", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	private void notifyDataChangeListeners() {
		for (DataChangeListener listener: dataChangeListeners) {
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
		
	}
	
	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("Entidade nulo");
		}
		txtId.setText(String.valueOf(entity.getId()));
		txtNome.setText(entity.getName());
		
	}
	
	public void setErroMessages(Map<String, String> erros) {
		Set<String> fields = erros.keySet();
		
		if (fields.contains("nome")) {
			labelErrorNome.setText(erros.get("nome"));
		}		
	}

}
