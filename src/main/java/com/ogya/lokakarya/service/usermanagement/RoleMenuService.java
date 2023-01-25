package com.ogya.lokakarya.service.usermanagement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletResponse;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ogya.lokakarya.entity.usermanagement.Menu;
import com.ogya.lokakarya.entity.usermanagement.RoleMenu;
import com.ogya.lokakarya.entity.usermanagement.Roles;
import com.ogya.lokakarya.exception.BusinessException;
import com.ogya.lokakarya.repository.usermanagement.MenuRepository;
import com.ogya.lokakarya.repository.usermanagement.RoleMenuRepository;
import com.ogya.lokakarya.repository.usermanagement.RolesRepository;
import com.ogya.lokakarya.repository.usermanagement.criteria.RoleMenuCriteriaRepository;
import com.ogya.lokakarya.util.PaginationList;
import com.ogya.lokakarya.util.PagingRequestWrapper;
import com.ogya.lokakarya.wrapper.usermanagement.RoleMenuWrapper;

@Service
@Transactional
public class RoleMenuService {
	@Autowired
	RoleMenuRepository roleMenuRepository;

	@Autowired
	RolesRepository rolesRepository;

	@Autowired
	MenuRepository menuRepository;

	@Autowired
	RoleMenuCriteriaRepository roleMenuCriteriaRepository;

	public PaginationList<RoleMenuWrapper, RoleMenu> ListWithPaging(PagingRequestWrapper request) {
		List<RoleMenu> roleMenuList = roleMenuCriteriaRepository.findByFilter(request);
		int fromIndex = (request.getPage()) * request.getSize();
		int toIndex = Math.min(fromIndex + request.getSize(), roleMenuList.size());
		Page<RoleMenu> roleMenuPage = new PageImpl<>(roleMenuList.subList(fromIndex, toIndex),
				PageRequest.of(request.getPage(), request.getSize()), roleMenuList.size());
		List<RoleMenuWrapper> roleMenuWrapperList = new ArrayList<>();
		for (RoleMenu entity : roleMenuPage) {
			roleMenuWrapperList.add(toWrapper(entity));
		}
		return new PaginationList<RoleMenuWrapper, RoleMenu>(roleMenuWrapperList, roleMenuPage);
	}

	public PaginationList<RoleMenuWrapper, RoleMenu> findAllWithPagination(int page, int size) {
		Pageable paging = PageRequest.of(page, size);
		Page<RoleMenu> roleMenuPage = roleMenuRepository.findAll(paging);
		List<RoleMenu> roleMenuList = roleMenuPage.getContent();
		List<RoleMenuWrapper> roleMenuWrapperList = toWrapperList(roleMenuList);
		return new PaginationList<RoleMenuWrapper, RoleMenu>(roleMenuWrapperList, roleMenuPage);
	}

	private RoleMenuWrapper toWrapper(RoleMenu entity) {
		RoleMenuWrapper wrapper = new RoleMenuWrapper();
		wrapper.setRoleMenuId(entity.getRoleMenuId());
		wrapper.setRoleId(entity.getRoles() != null ? entity.getRoles().getRoleId() : null);
		wrapper.setMenuId(entity.getMenu() != null ? entity.getMenu().getMenuId() : null);
		wrapper.setRoleName(entity.getRoles() != null ? entity.getRoles().getNama() : null);
		wrapper.setMenuName(entity.getMenu() != null ? entity.getMenu().getNama() : null);
		wrapper.setIsActive(entity.getIsActive());
		wrapper.setProgramName(entity.getProgramName());
		wrapper.setCreatedDate(entity.getCreatedDate());
		wrapper.setCreatedBy(entity.getCreatedBy());
		wrapper.setUpdatedDate(entity.getUpdatedDate());
		wrapper.setUpdatedBy(entity.getUpdatedBy());
		return wrapper;
	}

	private List<RoleMenuWrapper> toWrapperList(List<RoleMenu> entityList) {
		List<RoleMenuWrapper> wrapperList = new ArrayList<RoleMenuWrapper>();
		for (RoleMenu entity : entityList) {
			RoleMenuWrapper wrapper = toWrapper(entity);
			wrapperList.add(wrapper);
		}
		return wrapperList;
	}

	public List<RoleMenuWrapper> findAll() {
		List<RoleMenu> roleMenuList = roleMenuRepository.findAll(Sort.by(Order.by("roleMenuId")).ascending());
		return toWrapperList(roleMenuList);
	}

	private RoleMenu toEntity(RoleMenuWrapper wrapper) {
		RoleMenu entity = new RoleMenu();
		if (wrapper.getRoleMenuId() != null) {
			entity = roleMenuRepository.getReferenceById(wrapper.getRoleMenuId());
		}
		Optional<Roles> optionalRoles = rolesRepository.findById(wrapper.getRoleId());
		Roles roles = optionalRoles.isPresent() ? optionalRoles.get() : null;
		entity.setRoles(roles);
		Optional<Menu> optionalMenu = menuRepository.findById(wrapper.getMenuId());
		Menu menu = optionalMenu.isPresent() ? optionalMenu.get() : null;
		entity.setMenu(menu);
		entity.setIsActive(wrapper.getIsActive());
		entity.setProgramName(wrapper.getProgramName());
		entity.setCreatedDate(wrapper.getCreatedDate());
		entity.setCreatedBy(wrapper.getCreatedBy());
		entity.setUpdatedDate(wrapper.getUpdatedDate());
		entity.setUpdatedBy(wrapper.getUpdatedBy());
		return entity;
	}

	public RoleMenuWrapper save(RoleMenuWrapper wrapper) {
		if (roleMenuRepository.isExistRoleMenu(wrapper.getRoleId(), wrapper.getMenuId()) > 0) {
			throw new BusinessException("Cannot add role menu, same Role ID and Menu ID already exist");
		}
		if (roleMenuRepository.isExistRole(wrapper.getRoleId()) == 0) {
			throw new BusinessException("Cannot add role menu, Role ID not exist");
		}
		if (roleMenuRepository.isExistMenu(wrapper.getMenuId()) == 0) {
			throw new BusinessException("Cannot add role menu, Menu ID not exist");
		}
		RoleMenu roleMenu = roleMenuRepository.save(toEntity(wrapper));
		return toWrapper(roleMenu);
	}

	public void delete(Long id) {
		roleMenuRepository.deleteById(id);
	}

	public void ExportToPdf(HttpServletResponse response) throws Exception {
		// Call the findAll method to retrieve the data
		List<RoleMenu> data = roleMenuRepository.findAll();

		// Now create a new iText PDF document
		Document pdfDoc = new Document(PageSize.A4.rotate());
		PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, response.getOutputStream());
		pdfDoc.open();

		Paragraph title = new Paragraph("List Role Menu", new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
		title.setAlignment(Element.ALIGN_CENTER);
		pdfDoc.add(title);

		// Add the generation date
		pdfDoc.add(new Paragraph(
				"Report generated on: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())));

		// Create a table
		PdfPTable pdfTable = new PdfPTable(8);

		pdfTable.setWidthPercentage(100);
		pdfTable.setSpacingBefore(10f);
		pdfTable.setSpacingAfter(10f);

		pdfTable.addCell("Role");
		pdfTable.addCell("Menu");
		pdfTable.addCell("Is Active");
		pdfTable.addCell("Program Name");
		pdfTable.addCell("Created Date");
		pdfTable.addCell("Created By");
		pdfTable.addCell("Updated Date");
		pdfTable.addCell("Updated By");
		BaseColor color = new BaseColor(135, 206, 235);
		for (int i = 0; i < 8; i++) {
			pdfTable.getRow(0).getCells()[i].setBackgroundColor(color);
		}

		// Iterate through the data and add it to the table
		for (RoleMenu entity : data) {
			pdfTable.addCell(String
					.valueOf(entity.getRoles().getNama() != null ? String.valueOf(entity.getRoles().getNama()) : "-"));
			pdfTable.addCell(String
					.valueOf(entity.getMenu().getNama() != null ? String.valueOf(entity.getMenu().getNama()) : "-"));
			pdfTable.addCell(String.valueOf(entity.getIsActive() != null ? String.valueOf(entity.getIsActive()) : "-"));
			pdfTable.addCell(
					String.valueOf(entity.getProgramName() != null ? String.valueOf(entity.getProgramName()) : "-"));

			SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
			String createdDate = "-";
			if (entity.getCreatedDate() != null) {
				createdDate = formatter.format(entity.getCreatedDate());
			}
			pdfTable.addCell(createdDate);
			pdfTable.addCell(
					String.valueOf(entity.getCreatedBy() != null ? String.valueOf(entity.getCreatedBy()) : "-"));

			String updatedDate = "-";
			if (entity.getUpdatedDate() != null) {
				updatedDate = formatter.format(entity.getUpdatedDate());
			}
			pdfTable.addCell(updatedDate);
			pdfTable.addCell(
					String.valueOf(entity.getUpdatedBy() != null ? String.valueOf(entity.getUpdatedBy()) : "-"));

		}

		// Add the table to the pdf document
		pdfDoc.add(pdfTable);

		pdfDoc.close();
		pdfWriter.close();

		response.setContentType("application/pdf");
		response.setHeader("Content-Disposition", "attachment; filename=exportedPdf.pdf");
	}
}