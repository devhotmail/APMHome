/*
 */
package com.ge.apm.domain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;


/**
 *
 * @author 212601104
 */
@Entity
@Table(name = "asset_tag_biomed_group")
public class AssetTagBiomedGroup  implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "id")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "biomed_group_id")
    private Integer biomedGroupId;
    @Basic(optional = false)
    @NotNull
    @Column(name = "tag_id")
    private Integer tagId;

    @JoinColumn(name = "biomed_group_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private BiomedGroup biomedGroup;

    @JoinColumn(name = "tag_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    private AssetTag assetTag;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getBiomedGroupId() {
        return biomedGroupId;
    }

    public void setBiomedGroupId(Integer biomedGroupId) {
        this.biomedGroupId = biomedGroupId;
    }

    public Integer getTagId() {
        return tagId;
    }

    public void setTagId(Integer tagId) {
        this.tagId = tagId;
    }

    public BiomedGroup getBiomedGroup() {
        return biomedGroup;
    }

    public void setBiomedGroup(BiomedGroup biomedGroup) {
        this.biomedGroup = biomedGroup;
    }

    public AssetTag getAssetTag() {
        return assetTag;
    }

    public void setAssetTag(AssetTag assetTag) {
        this.assetTag = assetTag;
    }
}
