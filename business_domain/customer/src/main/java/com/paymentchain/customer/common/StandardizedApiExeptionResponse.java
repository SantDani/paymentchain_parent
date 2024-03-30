/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.paymentchain.customer.common;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Represents a standardized approach to REST API error reporting, supported by the <b>Internet Engineering Task Force (IETF)</b>,
 * an open standard organization that develops and promotes voluntary internet standards. This approach is defined in <b>RFC 7807</b> (https://datatracker.ietf.org/doc/html/rfc7807),
 * which introduces a generalized error-handling schema comprising five key components:
 * <ol>
 *   <li><b>type</b> - A URI identifier that categorizes the error.</li>
 *   <li><b>title</b> - A brief, human-readable message summarizing the error.</li>
 *   <li><b>code</b> - A unique error code associated with the specific error encountered.</li>
 *   <li><b>detail</b> - A detailed human-readable explanation of the error.</li>
 *   <li><b>instance</b> - A URI that identifies the specific occurrence of the error, which can be useful for logging and debugging purposes.</li>
 * </ol>
 * This schema aims to standardize error reporting across REST APIs, making it easier for client applications to understand and handle errors
 * in a consistent manner.
 * @author SantiagoSRP
 */
public class StandardizedApiExeptionResponse {

    /**
     * Example was with springFox, so we need migrating. More info
     * https://springdoc.org/#migrating-from-springfox Required is deprecated.
     * https://github.com/OpenAPITools/openapi-generator/issues/14398
     */

    @Schema(description = "The unique uri identifier that categorizes the error",
            name = "type",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "/errors/authentication/not-authorized")
    private String type;

    @Schema(description = "A brief, human-readable message about the error",
            name = "title",
            requiredMode = Schema.RequiredMode.REQUIRED,
            example = "The user does not have autorization")
    private String title;
    
    @Schema(description = "The unique error code",
            name = "code",
            requiredMode = Schema.RequiredMode.NOT_REQUIRED,
            example = "192")
    private String code;
    
    @Schema(description = "A human-readable explanation of the error", 
            name = "detail",
            requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "The user does not have the propertly persmissions to acces the "
            + "resource, please contact with ass Admin")
    private String detail;
    
    @Schema(description = "A URI that identifies the specific occurrence of the error", 
            name = "detail",
            requiredMode = Schema.RequiredMode.REQUIRED, 
            example = "/errors/authentication/not-authorized/01")
    private String instance = "/errors/uncategorized/bank";

    public StandardizedApiExeptionResponse() {
        this.type = "/errors/uncategorized";
    }

    public StandardizedApiExeptionResponse(String title, String code, String detail) {
        super();
        this.title = title;
        this.code = code;
        this.detail = detail;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStatus() {
        return code;
    }

    public void setStatus(String status) {
        this.code = status;
    }

    public String getDetail() {
        return detail;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}
