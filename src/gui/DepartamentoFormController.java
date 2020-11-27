package gui;

import java.net.URL;
import java.util.ResourceBundle;

import db.DbException;
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
import model.entities.Departamento;
import model.services.DepartamentoService;

public class DepartamentoFormController implements Initializable {
	
	private Departamento entity;
	
	private DepartamentoService service;

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
	
	public void setDepartamento(Departamento entity) {
		this.entity = entity;
	}
	
	public void setDepartamentoService(DepartamentoService service) {
		this.service = service;
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
			Utils.currentStage(event).close();
		}
		catch (DbException e) {
			Alertas.showAlert("Error ao salvar", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	private Departamento getFormData() {
		Departamento obj = new Departamento();
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		obj.setName(txtNome.getText());
		
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

}
