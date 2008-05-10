package tests;

public interface imanContentDAO {

	public imanContent get(int id);

	public imanContent[] getAll();

	public void insert(imanContent record);

	public void update(imanContent record);

	public void delete(imanContent record);
}
