package model.services;

import java.util.List;

import model.dao.DaoFactory;
import model.dao.VendedorDao;
import model.entities.Vendedor;

public class VendedorService {
	
	VendedorDao dao = DaoFactory.createSellerDao();
	
	public List<Vendedor> findAll() {
		return dao.findAll();
	}
	
	public void saveOrUpdate(Vendedor obj) {
		if (obj.getId() == null) {
			dao.insert(obj);
		}
		else {
			dao.update(obj);
		}
	}
	
	public void remove(Vendedor obj) {
		dao.deleteById(obj.getId());
	}
		

}
