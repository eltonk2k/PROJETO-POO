package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartamentoDao;
import model.entities.Departamento;

public class DepartamentoService {
	
	DepartamentoDao dao = DaoFactory.createDepartmentDao();
	
	public List<Departamento> findAll() {
		return dao.findAll();
	}

}
