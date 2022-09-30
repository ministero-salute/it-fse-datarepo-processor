package it.finanze.sanita.fse2.ms.edssrvdataprocessor.dto.response;

import lombok.Data;

/**
 *
 * @author Riccardo Bonesi
 *
 *	DTO used to return check exist result.
 */
@Data
public class ResourceExistResDTO extends ResponseDTO {


	/**
	 * Serial version uid.
	 */
	private static final long serialVersionUID = -1550025571939901939L;

	private boolean exist;

	public ResourceExistResDTO() {
		super();
		exist = false;
	}

	public ResourceExistResDTO(final LogTraceInfoDTO traceInfo, final boolean inExist) {
		super(traceInfo);
		exist = inExist;
	}
}
